package com.dmitrycherkes.notificationservice.model.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserMessageDto {
    private Long id;
    private String email;
    private String username;
}