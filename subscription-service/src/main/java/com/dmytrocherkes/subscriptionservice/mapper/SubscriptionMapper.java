package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;

import java.util.List;

public class SubscriptionMapper {

    public static Subscription toEntity(SubscriptionRequest request, City city) {
         Subscription subscription = Subscription.builder()
                .userId(request.getUserId())
                .city(city)
                .notifyBeforeHours(request.getNotifyBeforeHours())
                .isActive(request.getIsActive())
                .build();

        List<SubscriptionRule> rules = request.getRules().stream()
                .map(dto -> {
                    SubscriptionRule rule = SubscriptionRuleMapper.toEntity(dto);
                    rule.setSubscription(subscription);
                    return rule;
                })
                .toList();

        subscription.setRules(rules);
        return subscription;
    }

    public static SubscriptionResponse toResponse(Subscription entity) {
        return SubscriptionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .cityId(entity.getCity().getId())
                .notifyBeforeHours(entity.getNotifyBeforeHours())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .rules(entity.getRules().stream()
                        .map(SubscriptionRuleMapper::toDTO)
                        .toList())
                .build();
    }

    public static List<SubscriptionResponse> toResponseList(List<Subscription> entities) {
         return entities.stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }
}
