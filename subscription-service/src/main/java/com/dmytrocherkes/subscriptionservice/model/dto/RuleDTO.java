package com.dmytrocherkes.subscriptionservice.model.dto;

import com.dmytrocherkes.subscriptionservice.model.enums.ParameterType;
import com.dmytrocherkes.subscriptionservice.model.enums.RuleOperator;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDTO {
    private UUID id;

    @NotNull(message = "Parameter type is required")
    private ParameterType parameterType;

    @NotNull(message = "Operator is required")
    private RuleOperator operator;

    @NotNull(message = "Value 1 is required")
    private BigDecimal value1;

    private BigDecimal value2;
}
