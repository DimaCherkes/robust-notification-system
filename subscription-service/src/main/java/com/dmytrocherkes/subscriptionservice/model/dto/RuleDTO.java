package com.dmytrocherkes.subscriptionservice.model.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Parameter type is required")
    private String parameterType;

    @NotBlank(message = "Operator is required")
    private String operator;

    @NotNull(message = "Value 1 is required")
    private BigDecimal value1;

    private BigDecimal value2;
}
