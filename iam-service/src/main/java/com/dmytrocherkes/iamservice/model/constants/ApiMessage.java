package com.dmytrocherkes.iamservice.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiMessage {
    TOKEN_CREATED_OR_UPDATED("User's token has been created or updated"),
    USER_CREATED_OR_UPDATED("User has been created or updated"),
    USER_LOGIN_SUCCESSFUL("User has been logged in"),
    COMMENT_UPDATED("Comment has been updated"),
    ;

    private final String message;

}
