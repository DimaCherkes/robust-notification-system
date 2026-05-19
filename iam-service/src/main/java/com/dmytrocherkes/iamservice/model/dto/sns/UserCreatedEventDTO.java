package com.dmytrocherkes.iamservice.model.dto.sns;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedEventDTO {
    private Integer userId;
    private String email;
    private String username;
}
