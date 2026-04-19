package com.dmitrycherkes.decisionservice.service;

import com.dmitrycherkes.decisionservice.model.entity.AlertHistory;
import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import com.dmitrycherkes.decisionservice.repository.AlertHistoryRepository;
import com.dmitrycherkes.decisionservice.repository.SubscriptionRuleRepository;
import com.dmitrycherkes.decisionservice.repository.WeatherForecastRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DecisionService {

    private final SubscriptionRuleRepository subscriptionRuleRepository;
    private final WeatherForecastRepository weatherForecastRepository;
    private final AlertHistoryRepository alertHistoryRepository;

    @Scheduled(fixedDelayString = "30000")
    public void runDecisionProcess() {
        // todo: impl loop to iterate through all cities ids
        List<Integer> allCitiesIds = subscriptionRuleRepository.getAllCityIds();
        Integer cityId = 1;

        List<SubscriptionRule> rules = subscriptionRuleRepository.getAllByCityId(cityId);
        List<WeatherForecast> relevantForecasts = weatherForecastRepository.getAllByCityIdAndForecastTimeIsGreaterThan(cityId, OffsetDateTime.now());

        for (SubscriptionRule rule : rules) {
            log.info("Processing rule for city {}: {} {} {}",
                    rule.getCityId(), rule.getParameterType(), rule.getOperator(), rule.getValue1());

            for (WeatherForecast forecast : relevantForecasts) {
                if (isRuleSatisfied(rule, forecast)) {
                    log.info("!!! RULE SATISFIED for rule {} at {}: {} is {} (threshold: {})",
                            rule.getId(), forecast.getForecastTime(),
                            rule.getParameterType(), getValueForParameter(rule, forecast),
                            rule.getValue1());
                    // In real implementation, here we send a notification message to another service/queue
                    AlertHistory alert = AlertHistory.builder()
                            .triggeredAt(OffsetDateTime.now())
                            .rule(rule)
                            .forecastTime(forecast.getForecastTime())
                            .build();
                    alertHistoryRepository.save(alert);
                }
            }
        }
    }

    private boolean isRuleSatisfied(SubscriptionRule rule, WeatherForecast forecast) {
        BigDecimal actualValue = getValueForParameter(rule, forecast);
        if (actualValue == null) return false;

        return switch (rule.getOperator()) {
            case GREATER_THAN -> actualValue.compareTo(rule.getValue1()) > 0;
            case LESS_THAN -> actualValue.compareTo(rule.getValue1()) < 0;
            case EQUALS -> actualValue.compareTo(rule.getValue1()) == 0;
            case BETWEEN -> actualValue.compareTo(rule.getValue1()) >= 0
                    && actualValue.compareTo(rule.getValue2()) <= 0;
        };
    }

    private BigDecimal getValueForParameter(SubscriptionRule rule, WeatherForecast forecast) {
        return switch (rule.getParameterType()) {
            case TEMPERATURE -> forecast.getTemp();
            case HUMIDITY -> forecast.getHumidity() != null ? new BigDecimal(forecast.getHumidity()) : null;
            case WIND_SPEED -> forecast.getWindSpeed();
            case RAIN -> forecast.getPop();
        };
    }
}
