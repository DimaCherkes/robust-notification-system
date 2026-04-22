package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionDTO;

import java.util.List;

public class SubscriptionMapper {

    public static Subscription toEntity(SubscriptionRequest request, City city) {
        Subscription subscription = Subscription.builder()
                .createdByUserId(request.getUserId())
                .updatedByUserId(request.getUserId())
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

    public static SubscriptionDTO toDTO(Subscription entity) {
        return SubscriptionDTO.builder()
                .id(entity.getId())
                .cityId(entity.getCity().getId())
                .notifyBeforeHours(entity.getNotifyBeforeHours())
                .rules(entity.getRules().stream()
                        .map(SubscriptionRuleMapper::toDTO)
                        .toList())

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .updatedByUserId(entity.getUpdatedByUserId())
                .build();
    }

    public static List<SubscriptionDTO> toResponseList(List<Subscription> entities) {
        return entities.stream()
                .map(SubscriptionMapper::toDTO)
                .toList();
    }
}
