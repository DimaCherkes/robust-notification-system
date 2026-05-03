package com.dmytrocherkes.subscriptionservice.model.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {
    private UUID id;
    private Integer cityId;
    private String cityName;
    private Integer notifyBeforeHours;
    private Boolean isActive;
    private List<RuleDTO> rules;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Integer createdByUserId;
    private Integer updatedByUserId;
}
