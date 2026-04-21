package com.dmytrocherkes.subscriptionservice.model.response;

import com.dmytrocherkes.subscriptionservice.model.dto.RuleDTO;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private UUID id;
    private Integer userId;
    private Integer cityId;
    private String cityName;
    private Integer notifyBeforeHours;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<RuleDTO> rules;
}
