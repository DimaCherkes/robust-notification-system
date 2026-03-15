package com.dmytrocherkes.subscriptionservice.service;

import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(SubscriptionRequest request);
    SubscriptionResponse updateSubscription(UUID id, SubscriptionRequest request);
    SubscriptionResponse getSubscriptionById(UUID id);
    List<SubscriptionResponse> getAllByUserId(Long userId);
    void deleteSubscription(UUID id);
}
