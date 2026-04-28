package com.dmitrycherkes.decisionservice.service;

import com.dmitrycherkes.decisionservice.model.dto.SubscriptionMessageDTO;
import com.dmitrycherkes.decisionservice.model.entity.Subscription;
import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import com.dmitrycherkes.decisionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionSyncService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void upsertSubscription(SubscriptionMessageDTO dto) {
        log.info("Upserting subscription: {}", dto.getId());
        
        Subscription subscription = subscriptionRepository.findById(dto.getId())
                .orElse(new Subscription());
        
        subscription.setId(dto.getId());
        subscription.setUserId(dto.getCreatedByUserId());
        subscription.setCityId(dto.getCityId());
        subscription.setNotifyBeforeHours(dto.getNotifyBeforeHours());
        
        // Clear old rules and add new ones
        if (subscription.getRules() != null) {
            subscription.getRules().clear();
        }
        
        if (dto.getRules() != null) {
            subscription.setRules(dto.getRules().stream()
                    .map(r -> SubscriptionRule.builder()
                            .id(r.getId())
                            .subscription(subscription)
                            .parameterType(r.getParameterType())
                            .operator(r.getOperator())
                            .value1(r.getValue1())
                            .value2(r.getValue2())
                            .build())
                    .collect(Collectors.toList()));
        }
        
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(UUID id) {
        log.info("Hard deleting subscription from decision-service: {}", id);
        subscriptionRepository.deleteById(id);
    }
}
