package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.NotificationMessage;
import com.dmitrycherkes.weatherservice.model.entity.ActiveRuleCache;
import com.dmitrycherkes.weatherservice.model.entity.AlertHistory;
import com.dmitrycherkes.weatherservice.model.entity.WeatherHourly;
import com.dmitrycherkes.weatherservice.repository.ActiveRuleCacheRepository;
import com.dmitrycherkes.weatherservice.repository.AlertHistoryRepository;
import com.dmitrycherkes.weatherservice.repository.WeatherHourlyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConditionCheckerService {

    private final ActiveRuleCacheRepository activeRuleCacheRepository;
    private final WeatherHourlyRepository weatherHourlyRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void checkAllConditions() {
        log.info("Starting condition check for all active rules");
        List<ActiveRuleCache> rules = activeRuleCacheRepository.findAll();
        log.info("Found {} rules to evaluate", rules.size());

        for (ActiveRuleCache rule : rules) {
            evaluateRule(rule);
        }
    }

    private void evaluateRule(ActiveRuleCache rule) {
        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime targetTime = now.plusHours(rule.getNotifyBeforeHours());

        weatherHourlyRepository.findClosestForecast(rule.getCity().getCityId(), targetTime)
                .ifPresent(weather -> {
                    // Check if the forecast is within a reasonable range (e.g., within 2 hours of target)
                    if (Math.abs(ChronoUnit.HOURS.between(targetTime, weather.getForecastTime())) <= 2) {
                        if (isConditionMet(rule, weather)) {
                            triggerAlertIfNeeded(rule, weather);
                        }
                    }
                });
    }

    private boolean isConditionMet(ActiveRuleCache rule, WeatherHourly weather) {
        Integer actualValue = getActualValue(rule, weather);
        if (actualValue == null) return false;

        return switch (rule.getOperator()) {
            case GREATER_THAN -> actualValue.compareTo(rule.getValue1()) > 0;
            case LESS_THAN -> actualValue.compareTo(rule.getValue1()) < 0;
            case EQUALS -> actualValue.compareTo(rule.getValue1()) == 0;
            case BETWEEN -> actualValue.compareTo(rule.getValue1()) >= 0 && actualValue.compareTo(rule.getValue2()) <= 0;
        };
    }

    private Integer getActualValue(ActiveRuleCache rule, WeatherHourly weather) {
        return switch (rule.getParameterType()) {
            case TEMPERATURE -> weather.getTemp();
            case HUMIDITY -> weather.getHumidity() != null ? weather.getHumidity() : null;
            case RAIN -> weather.getPop();
            case WIND_SPEED -> weather.getWindSpeed();
        };
    }

    private void triggerAlertIfNeeded(ActiveRuleCache rule, WeatherHourly weather) {
        if (!alertHistoryRepository.existsByRuleRuleIdAndForecastTime(rule.getRuleId(), weather.getForecastTime())) {
            log.info("Condition MET for rule {} in city {}. Triggering notification.", 
                    rule.getRuleId(), rule.getCity().getName());

            String message = String.format("Alert for city %s: %s is %s %s at %s", 
                    rule.getCity().getName(), 
                    rule.getParameterType(), 
                    rule.getOperator(), 
                    rule.getValue1() + (rule.getValue2() != null ? " and " + rule.getValue2() : ""),
                    weather.getForecastTime());

            NotificationMessage notification = NotificationMessage.builder()
                    .userId(rule.getUserId())
                    .message(message)
                    .ruleId(rule.getRuleId())
                    .cityId(rule.getCity().getCityId())
                    .build();

            notificationProducer.sendNotification(notification);

            AlertHistory history = AlertHistory.builder()
                    .rule(rule)
                    .forecastTime(weather.getForecastTime())
                    .build();
            alertHistoryRepository.save(history);
        }
    }
}
