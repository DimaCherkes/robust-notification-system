package com.dmytrocherkes.subscriptionservice.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiLogMessage {
    // spring cloud OpenFeign
    SEND_REQUEST_TO_IAM_SERVICE_USERNAME("Sending request to IAM service, username: {}"),

    // service log messages
    CREATING_SUBSCRIPTION("Creating subscription for user {} and city {}"),
    UPDATING_SUBSCRIPTION("Updating subscription with id: {}"),
    FETCHING_SUBSCRIPTION_BY_ID("Fetching subscription with id: {}"),
    FETCHING_ALL_SUBSCRIPTIONS_BY_USER_ID("Fetching all subscriptions for user ID: {}"),
    SOFT_DELETING_SUBSCRIPTION("Soft deleting subscription with id: {}"),
    HARD_DELETING_SUBSCRIPTION("Hard deleting subscription with id: {}"),

    // controller log messages
    REST_CREATE_SUBSCRIPTION("REST request to create subscription for user {}"),
    REST_GET_SUBSCRIPTION_BY_ID("REST request to get subscription: {}"),
    REST_GET_SUBSCRIPTIONS_BY_USER("REST request to get subscriptions for user: {}"),
    REST_UPDATE_SUBSCRIPTION("REST request to update subscription: {}"),
    REST_SOFT_DELETE_SUBSCRIPTION("REST request to soft delete subscription: {}"),
    REST_HARD_DELETE_SUBSCRIPTION("REST request to hard delete subscription: {}"),

    // city controller
    REST_GET_ALL_CITIES("REST request to get cities by user: {}"),
    ;

    private final String value;
}
