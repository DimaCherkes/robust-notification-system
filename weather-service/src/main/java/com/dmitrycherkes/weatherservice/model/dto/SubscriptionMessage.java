package com.dmitrycherkes.weatherservice.model.dto;

import com.dmitrycherkes.weatherservice.model.enums.ParameterType;
import com.dmitrycherkes.weatherservice.model.enums.RuleOperator;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionMessage {
    private UUID subscriptionId;
    private Long userId;
    private Integer cityId;
    private String cityName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String timezone;
    private Integer notifyBeforeHours;
    private Boolean isActive;
    private List<RuleMessage> rules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RuleMessage {
        private UUID ruleId;
        private ParameterType parameterType;
        private RuleOperator operator;
        private Integer value1;
        private Integer value2;
    }
}
