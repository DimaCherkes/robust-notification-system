package com.dmitrycherkes.decisionservice.utils;

import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import com.dmitrycherkes.decisionservice.model.enums.ParameterType;
import com.dmitrycherkes.decisionservice.model.enums.RuleOperator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class EntityFactory {

//    public static List<SubscriptionRule> createSampleRules() {
//        return List.of(
//            SubscriptionRule.builder()
//                .parameterType(ParameterType.TEMPERATURE)
//                .operator(RuleOperator.GREATER_THAN)
//                .value1(new BigDecimal("25.0"))
//                .notifyBeforeHours(24)
//                .build(),
//            SubscriptionRule.builder()
//                .id(2)
//                .subscriptionId(2)
//                .userId(2)
//                .cityId(2)
//                .parameterType(ParameterType.WIND_SPEED)
//                .operator(RuleOperator.LESS_THAN)
//                .value1(new BigDecimal("10.0"))
//                .notifyBeforeHours(12)
//                .build()
//        );
//    }
//
//    public static List<WeatherForecast> createSampleForecasts() {
//        return List.of(
//            WeatherForecast.builder()
//                .cityId(1)
//                .forecastTime(OffsetDateTime.now().plusHours(5).truncatedTo(ChronoUnit.HOURS))
//                .temp(new BigDecimal("27.5"))
//                .humidity(60)
//                .windSpeed(new BigDecimal("5.0"))
//                .pop(new BigDecimal("0.1"))
//                .weatherMain("Clear")
//                .build(),
//            WeatherForecast.builder()
//                .cityId(2)
//                .forecastTime(OffsetDateTime.now().plusHours(3).truncatedTo(ChronoUnit.HOURS))
//                .temp(new BigDecimal("22.0"))
//                .humidity(55)
//                .windSpeed(new BigDecimal("12.0"))
//                .pop(new BigDecimal("0.0"))
//                .weatherMain("Clouds")
//                .build()
//        );
//    }
}
