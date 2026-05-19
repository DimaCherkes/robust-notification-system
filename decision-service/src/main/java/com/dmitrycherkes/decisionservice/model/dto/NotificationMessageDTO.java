package com.dmitrycherkes.decisionservice.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessageDTO {
    private Integer userId;
    private UUID subscriptionId;
    private String subject;
    private String content;
}
