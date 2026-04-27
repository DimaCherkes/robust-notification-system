package com.dmitrycherkes.decisionservice.service;

import com.dmitrycherkes.decisionservice.model.entity.AlertHistory;
import com.dmitrycherkes.decisionservice.model.entity.Subscription;
import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import com.dmitrycherkes.decisionservice.model.enums.ParameterType;
import com.dmitrycherkes.decisionservice.model.enums.RuleOperator;
import com.dmitrycherkes.decisionservice.repository.AlertHistoryRepository;
import com.dmitrycherkes.decisionservice.repository.SubscriptionRepository;
import com.dmitrycherkes.decisionservice.repository.WeatherForecastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DecisionService {

    private final SubscriptionRepository subscriptionRepository;
    private final WeatherForecastRepository weatherForecastRepository;
    private final AlertHistoryRepository alertHistoryRepository;

    @Scheduled(fixedDelayString = "${app.decision.process-delay-ms:60000}")
    @Transactional
    public void runDecisionProcess() {
        log.info("Starting decision process...");
        List<Integer> cityIds = subscriptionRepository.findDistinctCityIds();

        for (Integer cityId : cityIds) {
            processCity(cityId);
        }
        log.info("Decision process completed.");
    }

    private void processCity(Integer cityId) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByCityId(cityId);
        if (subscriptions.isEmpty()) return;

        // Fetch all forecasts for this city that are in the future
        List<WeatherForecast> forecasts = weatherForecastRepository.findAllByCityIdAndForecastTimeAfter(cityId, OffsetDateTime.now());
        if (forecasts.isEmpty()) return;

        for (Subscription subscription : subscriptions) {
            processSubscription(subscription, forecasts);
        }
    }

    private void processSubscription(Subscription subscription, List<WeatherForecast> forecasts) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime maxNotificationTime = now.plusHours(subscription.getNotifyBeforeHours());

        // Filter forecasts that fall within the notification window
        List<WeatherForecast> relevantForecasts = forecasts.stream()
                .filter(f -> f.getForecastTime().isBefore(maxNotificationTime) || f.getForecastTime().isEqual(maxNotificationTime))
                .toList();

        for (SubscriptionRule rule : subscription.getRules()) {
            for (WeatherForecast forecast : relevantForecasts) {
                if (isRuleSatisfied(rule, forecast)) {
                    triggerAlert(rule, forecast);
                }
            }
        }
    }

    private void triggerAlert(SubscriptionRule rule, WeatherForecast forecast) {
        // Check if we already triggered an alert for this rule and this specific forecast time
        if (alertHistoryRepository.existsByRuleIdAndForecastTime(rule.getId(), forecast.getForecastTime())) {
            return;
        }

        log.info("Rule satisfied! Subscription: {}, Rule: {}, City: {}, Time: {}", 
                rule.getSubscription().getId(), rule.getId(), rule.getSubscription().getCityId(), forecast.getForecastTime());

        AlertHistory alert = AlertHistory.builder()
                .ruleId(rule.getId())
                .forecastTime(forecast.getForecastTime())
                .triggeredAt(OffsetDateTime.now())
                .build();
        
        alertHistoryRepository.save(alert);
        
        // TODO: Send notification to SNS/SQS for Notification Service
    }

    private boolean isRuleSatisfied(SubscriptionRule rule, WeatherForecast forecast) {
        BigDecimal actualValue = getValueForParameter(rule, forecast);
        if (actualValue == null) return false;

        RuleOperator operator = rule.getOperator();

        return switch (operator) {
            case GREATER_THAN -> actualValue.compareTo(rule.getValue1()) > 0;
            case LESS_THAN -> actualValue.compareTo(rule.getValue1()) < 0;
            case EQUALS -> actualValue.compareTo(rule.getValue1()) == 0;
            case BETWEEN -> actualValue.compareTo(rule.getValue1()) >= 0
                    && actualValue.compareTo(rule.getValue2()) <= 0;
        };
    }

    private BigDecimal getValueForParameter(SubscriptionRule rule, WeatherForecast forecast) {
        ParameterType type = rule.getParameterType();
        return switch (type) {
            case TEMPERATURE -> forecast.getTemp();
            case HUMIDITY -> forecast.getHumidity() != null ? new BigDecimal(forecast.getHumidity()) : null;
            case WIND_SPEED -> forecast.getWindSpeed();
            case RAIN -> forecast.getPop();
        };
    }
}
