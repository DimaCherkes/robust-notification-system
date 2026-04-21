package com.dmytrocherkes.subscriptionservice.service;

import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionMapper;
import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionRuleMapper;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiErrorMessage;
import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.dto.UserDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import com.dmytrocherkes.subscriptionservice.model.exception.ResourceNotFoundException;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.IamApiResponse;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;
import com.dmytrocherkes.subscriptionservice.repository.CityRepository;
import com.dmytrocherkes.subscriptionservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl {

    private final SubscriptionRepository subscriptionRepository;
    private final CityRepository cityRepository;
    private final IamClient iamClient;

    @Transactional
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        log.trace(ApiLogMessage.CREATING_SUBSCRIPTION.getValue(), request.getUserId(), request.getCityId());

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.CITY_NOT_FOUND_BY_ID.getMessage(request.getCityId())));

        Subscription subscription = SubscriptionMapper.toEntity(request, city);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return SubscriptionMapper.toResponse(savedSubscription);
    }

    @Transactional
    public SubscriptionResponse updateSubscription(UUID id, SubscriptionRequest request) {
        log.trace(ApiLogMessage.UPDATING_SUBSCRIPTION.getValue(), id);

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
                        SubscriptionRule rule = SubscriptionRuleMapper.toEntity(ruleDTO);
                        rule.setSubscription(subscription);
                        return rule;
                    })
                    .toList();
            subscription.getRules().addAll(newRules);
        }

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return SubscriptionMapper.toResponse(updatedSubscription);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionById(UUID id) {
        log.trace(ApiLogMessage.FETCHING_SUBSCRIPTION_BY_ID.getValue(), id);
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id)));
        return SubscriptionMapper.toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAllByUsername(String username) {
        log.trace(ApiLogMessage.SEND_REQUEST_TO_IAM_SERVICE_USERNAME.getValue(), username);
        IamApiResponse<UserDTO> userByUsername = iamClient.getUserByUsername(username);

        log.info(ApiLogMessage.FETCHING_ALL_SUBSCRIPTIONS_BY_USER_ID.getValue(), userByUsername.getBody().getId());
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(userByUsername.getBody().getId());
        return SubscriptionMapper.toResponseList(subscriptions);
    }

    @Transactional
    public void deleteSubscription(UUID id) {
        log.trace(ApiLogMessage.DELETING_SUBSCRIPTION.getValue(), id);
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException(ApiErrorMessage.SUBSCRIPTION_NOT_FOUND_BY_ID.getMessage(id));
        }
        subscriptionRepository.deleteById(id);
    }
}
