package com.dmitrycherkes.decisionservice.model.dto;

import com.dmitrycherkes.decisionservice.model.enums.ParameterType;
import com.dmitrycherkes.decisionservice.model.enums.RuleOperator;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RuleMessageDTO {
    private UUID id;
    private ParameterType parameterType;
    private RuleOperator operator;
    private BigDecimal value1;
    private BigDecimal value2;
}
