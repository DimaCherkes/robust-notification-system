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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.aws.sqs.subscription-queue}")
    private String subscriptionQueue;

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
                .filter(f -> !f.getForecastTime().isAfter(maxNotificationTime))
                .toList();

        for (WeatherForecast forecast : relevantForecasts) {
            // Check if we already triggered an alert for this subscription and this specific forecast time
            if (alertHistoryRepository.existsBySubscriptionIdAndForecastTime(subscription.getId(), forecast.getForecastTime())) {
                continue;
            }

            // All rules must be satisfied (AND logic)
            boolean allRulesSatisfied = subscription.getRules().stream()
                    .allMatch(rule -> isRuleSatisfied(rule, forecast));

            if (allRulesSatisfied && !subscription.getRules().isEmpty()) {
                triggerAlert(subscription, forecast);
                break;
            }
        }
    }

    private void triggerAlert(Subscription subscription, WeatherForecast forecast) {
        log.info("All rules satisfied! Subscription: {}, City: {}, Forecast Time: {}",
                subscription.getId(), subscription.getCityId(), forecast.getForecastTime());

        AlertHistory alert = AlertHistory.builder()
                .subscriptionId(subscription.getId())
                .forecastTime(forecast.getForecastTime())
                .triggeredAt(OffsetDateTime.now())
                .build();

        alertHistoryRepository.save(alert);

        // Deactivate subscription to prevent spamming
        deactivateSubscription(subscription);

        // TODO: Send notification to SNS/SQS for Notification Service
    }

    private void deactivateSubscription(Subscription subscription) {
        log.info("Sending deactivation event for subscription: {}", subscription.getId());

        try {
            String message = objectMapper.writeValueAsString(subscription.getId());

            sqsTemplate.send(to -> to
                    .queue(subscriptionQueue)
                    .payload(message)
                    .header("action", "subscription_deactivate"));

            log.info("Deactivation event sent. Deleting subscription {} from local DB.", subscription.getId());
            subscriptionRepository.delete(subscription);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message payload", e);
            throw new RuntimeException("SQS serialization error", e);
        } catch (Exception e) {
            log.error("Failed to send deactivation event for subscription: {}", subscription.getId(), e);
        }
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
