package com.dmytrocherkes.iamservice.model.request;

import com.dmytrocherkes.iamservice.model.enums.RegistrationStatus;
import com.dmytrocherkes.iamservice.model.enums.UserSortField;
import lombok.Data;

@Data
public class UserSearchRequest {

    private String username;
    private String email;
    private RegistrationStatus registrationStatus;

    private String keyword;
    private UserSortField sortField;
}
