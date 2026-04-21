package com.dmytrocherkes.subscriptionservice.model.request;

import com.dmytrocherkes.subscriptionservice.model.dto.RuleDTO;
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

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "City ID is required")
    private Integer cityId;

    @Min(value = 0, message = "Notify before hours must be at least 0")
    private Integer notifyBeforeHours;

    private Boolean isActive;

    private List<RuleDTO> rules;
}
