package com.dmytrocherkes.subscriptionservice.model.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiErrorMessage {
    CITY_NOT_FOUND_BY_ID("City with ID: %s was not found"),
    SUBSCRIPTION_NOT_FOUND_BY_ID("Subscription with ID: %s was not found"),
    UNEXPECTED_ERROR_OCCURRED("An unexpected error occurred. Please try again later.");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
