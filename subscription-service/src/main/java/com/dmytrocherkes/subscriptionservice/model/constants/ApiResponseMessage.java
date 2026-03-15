package com.dmytrocherkes.subscriptionservice.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiResponseMessage {
    SUBSCRIPTION_CREATED("Subscription has been created"),
    SUBSCRIPTION_UPDATED("Subscription has been updated"),
    SUBSCRIPTION_DELETED("Subscription has been deleted");

    private final String message;
}
