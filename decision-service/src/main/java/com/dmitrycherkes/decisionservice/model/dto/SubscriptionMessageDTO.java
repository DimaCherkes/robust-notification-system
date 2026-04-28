package com.dmitrycherkes.decisionservice.model.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SubscriptionMessageDTO {
    private UUID id;
    private Integer cityId;
    private Integer createdByUserId;
    private Integer notifyBeforeHours;
    private List<RuleMessageDTO> rules;
}
