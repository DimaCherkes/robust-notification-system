package com.dmytrocherkes.subscriptionservice.service.impl;

import com.dmytrocherkes.subscriptionservice.model.exception.ResourceNotFoundException;
import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionMapper;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiErrorMessage;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import com.dmytrocherkes.subscriptionservice.repository.CityRepository;
import com.dmytrocherkes.subscriptionservice.repository.SubscriptionRepository;
import com.dmytrocherkes.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        log.info(ApiLogMessage.CREATING_SUBSCRIPTION.getValue(), request.getUserId(), request.getCityId());
        
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.CITY_NOT_FOUND_BY_ID.getMessage(request.getCityId())));

        Subscription subscription = subscriptionMapper.toEntity(request);
        subscription.setCity(city);
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(savedSubscription);
    }

    @Override
    @Transactional
    public SubscriptionResponse updateSubscription(UUID id, SubscriptionRequest request) {
        log.info(ApiLogMessage.UPDATING_SUBSCRIPTION.getValue(), id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.CITY_NOT_FOUND_BY_ID.getMessage(request.getCityId())));

        subscription.setUserId(request.getUserId());
        subscription.setCity(city);
        subscription.setNotifyBeforeHours(request.getNotifyBeforeHours());
        subscription.setIsActive(request.getIsActive() != null ? request.getIsActive() : subscription.getIsActive());
        subscription.setUpdatedAt(OffsetDateTime.now());

        // Sync rules
        subscription.getRules().clear();
        if (request.getRules() != null) {
            List<SubscriptionRule> newRules = request.getRules().stream()
                    .map(ruleDTO -> {
                        SubscriptionRule rule = subscriptionMapper.toRuleEntity(ruleDTO);
                        rule.setSubscription(subscription);
                        return rule;
                    })
                    .collect(Collectors.toList());
            subscription.getRules().addAll(newRules);
        }

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return subscriptionMapper.toResponse(updatedSubscription);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID id) {
        log.info(ApiLogMessage.FETCHING_SUBSCRIPTION_BY_ID.getValue(), id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));
        return subscriptionMapper.toResponse(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAllByUserId(Long userId) {
        log.info(ApiLogMessage.FETCHING_ALL_SUBSCRIPTIONS_BY_USER.getValue(), userId);
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        return subscriptionMapper.toResponseList(subscriptions);
    }

    @Override
    @Transactional
    public void deleteSubscription(UUID id) {
        log.info(ApiLogMessage.DELETING_SUBSCRIPTION.getValue(), id);
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id));
        }
        subscriptionRepository.deleteById(id);
    }
}
