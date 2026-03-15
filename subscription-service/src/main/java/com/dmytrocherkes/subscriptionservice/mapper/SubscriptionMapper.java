package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.dto.RuleDTO;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {

    @Mapping(target = "city.id", source = "cityId")
    @Mapping(target = "rules", source = "rules")
    Subscription toEntity(SubscriptionRequest request);

    @Mapping(target = "cityId", source = "city.id")
    @Mapping(target = "cityName", source = "city.name")
    SubscriptionResponse toResponse(Subscription entity);

    List<SubscriptionResponse> toResponseList(List<Subscription> entities);

    SubscriptionRule toRuleEntity(RuleDTO dto);

    RuleDTO toRuleDTO(SubscriptionRule entity);

    @AfterMapping
    default void setSubscriptionToRules(@MappingTarget Subscription subscription) {
        if (subscription.getRules() != null) {
            subscription.getRules().forEach(rule -> rule.setSubscription(subscription));
        }
    }
}
