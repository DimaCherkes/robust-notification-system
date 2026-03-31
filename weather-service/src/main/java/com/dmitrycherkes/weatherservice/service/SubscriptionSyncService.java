package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.SubscriptionMessage;
import com.dmitrycherkes.weatherservice.model.entity.ActiveRuleCache;
import com.dmitrycherkes.weatherservice.model.entity.MonitoredCity;
import com.dmitrycherkes.weatherservice.repository.ActiveRuleCacheRepository;
import com.dmitrycherkes.weatherservice.repository.MonitoredCityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionSyncService {

    private final MonitoredCityRepository monitoredCityRepository;
    private final ActiveRuleCacheRepository activeRuleCacheRepository;

    @Transactional
    public void processSubscriptionMessage(SubscriptionMessage message) {
        log.info("Processing subscription message for subscription ID: {}, city: {}", 
                message.getSubscriptionId(), message.getCityName());

        // 1. Update monitored_cities
        MonitoredCity city = monitoredCityRepository.findById(message.getCityId())
                .orElse(MonitoredCity.builder()
                        .cityId(message.getCityId())
                        .name(message.getCityName())
                        .latitude(message.getLatitude())
                        .longitude(message.getLongitude())
                        .timezone(message.getTimezone())
                        .build());

        // This is simplified. In a real system, you might want to track 
        // if ANY subscription is active for this city.
        // For now, we'll just upsert it.
        monitoredCityRepository.save(city);

        // 2. Sync active_rules_cache
//        activeRuleCacheRepository.deleteBySubscriptionId(message.getSubscriptionId());

        if (Boolean.TRUE.equals(message.getIsActive()) && message.getRules() != null) {
            List<ActiveRuleCache> rules = message.getRules().stream()
                    .map(ruleMsg -> ActiveRuleCache.builder()
                            .ruleId(ruleMsg.getRuleId())
                            .subscriptionId(message.getSubscriptionId())
                            .city(city)
                            .userId(message.getUserId())
                            .parameterType(ruleMsg.getParameterType())
                            .operator(ruleMsg.getOperator())
                            .value1(ruleMsg.getValue1())
                            .value2(ruleMsg.getValue2())
                            .notifyBeforeHours(message.getNotifyBeforeHours())
                            .build())
                    .collect(Collectors.toList());
            activeRuleCacheRepository.saveAll(rules);
            log.info("Synced {} rules for subscription ID: {}", rules.size(), message.getSubscriptionId());
        } else {
            log.info("Subscription ID: {} is inactive or has no rules, rules removed from cache", 
                    message.getSubscriptionId());
        }
    }
}
