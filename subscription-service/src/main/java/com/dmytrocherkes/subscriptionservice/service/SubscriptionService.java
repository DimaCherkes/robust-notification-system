package com.dmytrocherkes.subscriptionservice.service;

import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionMapper;
import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionRuleMapper;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiErrorMessage;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import com.dmytrocherkes.subscriptionservice.model.enums.AwsMessageTypes;
import com.dmytrocherkes.subscriptionservice.model.exception.ResourceNotFoundException;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionDTO;
import com.dmytrocherkes.subscriptionservice.repository.CityRepository;
import com.dmytrocherkes.subscriptionservice.repository.SubscriptionRepository;
import com.dmytrocherkes.subscriptionservice.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final SnsPublisher snsPublisher;
    private final CityService cityService;

    @Value("${app.aws.sns.subscription-topic-arn}")
    private String subscriptionTopicArn;

    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionRequest request) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.trace(ApiLogMessage.CREATING_SUBSCRIPTION.getValue(), userId, request.getCityId());

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.CITY_NOT_FOUND_BY_ID.getMessage(request.getCityId())));

        // increment active subscriptions count
        cityService.updateActiveSubscriptionsCount(city, city.getActiveSubscriptionsCount() + 1);

        Subscription subscription = SubscriptionMapper.toEntity(request, city);
        subscription.setCreatedByUserId(userId);
        subscription.setUpdatedByUserId(userId);
        subscription.setCreatedAt(OffsetDateTime.now());
        subscription.setUpdatedAt(OffsetDateTime.now());
        
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        SubscriptionDTO subscriptionDto = SubscriptionMapper.toDTO(savedSubscription);
        subscriptionDto.setCityName(city.getName());

        // send event to decision-consume-queue
        snsPublisher.publishMessage(
                subscriptionTopicArn,
                subscriptionDto,
                Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.SUBSCRIPTION_CREATED.getType())
        );

        return subscriptionDto;
    }

    @Transactional
    public SubscriptionDTO updateSubscription(UUID id, SubscriptionRequest request) {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.trace(ApiLogMessage.UPDATING_SUBSCRIPTION.getValue(), id);

        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.CITY_NOT_FOUND_BY_ID.getMessage(request.getCityId())));

        subscription.setUpdatedByUserId(userId);
        subscription.setCity(city);
        subscription.setNotifyBeforeHours(request.getNotifyBeforeHours());
        subscription.setIsActive(request.getIsActive() != null ? request.getIsActive() : subscription.getIsActive());
        subscription.setUpdatedAt(OffsetDateTime.now());

        // Sync rules
        subscription.getRules().clear();
        if (request.getRules() != null) {
            List<SubscriptionRule> newRules = request.getRules().stream()
                    .map(ruleDTO -> {
                        SubscriptionRule rule = SubscriptionRuleMapper.toEntity(ruleDTO);
                        rule.setSubscription(subscription);
                        return rule;
                    })
                    .toList();
            subscription.getRules().addAll(newRules);
        }

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        SubscriptionDTO updatedSubscriptionDTO = SubscriptionMapper.toDTO(updatedSubscription);

        // send event to decision-consume-queue
        snsPublisher.publishMessage(
                subscriptionTopicArn,
                updatedSubscriptionDTO,
                Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.SUBSCRIPTION_UPDATED.getType())
        );

        return updatedSubscriptionDTO;
    }

    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscriptionById(UUID id) {
        log.trace(ApiLogMessage.FETCHING_SUBSCRIPTION_BY_ID.getValue(), id);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));
        return SubscriptionMapper.toDTO(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllForCurrentUser() {
        Integer userId = SecurityUtils.getCurrentUserId();
        log.info(ApiLogMessage.FETCHING_ALL_SUBSCRIPTIONS_BY_USER_ID.getValue(), userId);
        List<Subscription> subscriptions = subscriptionRepository.findAllByCreatedByUserId(userId);
        return SubscriptionMapper.toResponseList(subscriptions);
    }

    @Transactional
    public void softDeleteSubscription(UUID id) {
        log.trace(ApiLogMessage.SOFT_DELETING_SUBSCRIPTION.getValue(), id);
        Subscription subscription = subscriptionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));

        subscription.setIsActive(false);
        subscription.setUpdatedAt(OffsetDateTime.now());
        subscriptionRepository.save(subscription);

        // decrement active subscriptions count
        City city = subscription.getCity();
        cityService.updateActiveSubscriptionsCount(city, city.getActiveSubscriptionsCount() - 1);

        // send event to decision-consume-queue
        snsPublisher.publishMessage(
                subscriptionTopicArn,
                id,
                Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.SUBSCRIPTION_DELETED.getType())
        );
    }

    @Transactional
    public void hardDeleteSubscription(UUID id) {
        log.trace(ApiLogMessage.HARD_DELETING_SUBSCRIPTION.getValue(), id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));

        City city = subscription.getCity();
        subscriptionRepository.hardDeleteById(id);

        // decrement active subscriptions count
        cityService.updateActiveSubscriptionsCount(city, city.getActiveSubscriptionsCount() - 1);

        // send event to decision-consume-queue
        snsPublisher.publishMessage(
                subscriptionTopicArn,
                id,
                Map.of(AwsMessageTypes.ACTION.getType(), AwsMessageTypes.SUBSCRIPTION_DELETED.getType())
        );
    }
}
