package com.dmytrocherkes.subscriptionservice.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiLogMessage {
    CREATING_SUBSCRIPTION("Creating subscription for user {} and city {}"),
    UPDATING_SUBSCRIPTION("Updating subscription with id: {}"),
    FETCHING_SUBSCRIPTION_BY_ID("Fetching subscription with id: {}"),
    FETCHING_ALL_SUBSCRIPTIONS_BY_USER("Fetching all subscriptions for user: {}"),
    DELETING_SUBSCRIPTION("Deleting subscription with id: {}"),
    REST_CREATE_SUBSCRIPTION("REST request to create subscription for user {}"),
    REST_GET_SUBSCRIPTION_BY_ID("REST request to get subscription: {}"),
    REST_GET_SUBSCRIPTIONS_BY_USER("REST request to get subscriptions for user: {}"),
    REST_UPDATE_SUBSCRIPTION("REST request to update subscription: {}"),
    REST_DELETE_SUBSCRIPTION("REST request to delete subscription: {}");

    private final String value;
}
