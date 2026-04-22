package com.dmytrocherkes.subscriptionservice.model.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AwsMessageTypes {
    ACTION("action"),
    CITY_ACTIVATE("city_activate"),
    CITY_DEACTIVATE("city_deactivate"),
    SUBSCRIPTION_CREATED("subscription_created"),
    SUBSCRIPTION_UPDATED("subscription_updated"),
    SUBSCRIPTION_DELETED("subscription_deleted"),
    ;

    private final String messageType;

    public String getType() {
        return String.format(messageType);
    }
}
