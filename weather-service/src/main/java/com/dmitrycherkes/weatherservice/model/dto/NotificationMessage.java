package com.dmitrycherkes.weatherservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationMessage {
    private Long userId;
    private String message;
    private UUID ruleId;
    private Integer cityId;
}
