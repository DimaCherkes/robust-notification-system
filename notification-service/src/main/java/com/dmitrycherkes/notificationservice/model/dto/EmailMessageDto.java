package com.dmitrycherkes.notificationservice.model.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EmailMessageDto {
    private Long userId;
    private UUID subscriptionId;
    private String subject;
    private String content;
}