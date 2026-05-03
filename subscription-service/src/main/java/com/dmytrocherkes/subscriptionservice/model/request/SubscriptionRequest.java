package com.dmytrocherkes.subscriptionservice.model.request;

import com.dmytrocherkes.subscriptionservice.model.dto.RuleDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRequest {

    private Integer userId;

    @NotNull(message = "City ID is required")
    private Integer cityId;

    @Min(value = 0, message = "Notify before hours must be at least 0")
    @Max(value = 48, message = "Notify before hours must be less than 48")
    private Integer notifyBeforeHours;

    private Boolean isActive;

    private List<RuleDTO> rules;
}
