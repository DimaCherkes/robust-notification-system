package com.dmytrocherkes.iamservice.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AwsMessageTypes {
    ACTION("action"),
    USER_UPSERT("user_upsert"),
    USER_DELETE("user_delete"),
    ;

    private final String messageType;

    public String getType() {
        return String.format(messageType);
    }
}
