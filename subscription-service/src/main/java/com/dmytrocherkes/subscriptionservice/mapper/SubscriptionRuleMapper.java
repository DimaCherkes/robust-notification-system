package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.dto.RuleDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;

public class SubscriptionRuleMapper {

    public static SubscriptionRule toEntity(RuleDTO dto) {
        return SubscriptionRule.builder()
                .id(dto.getId())
                .parameterType(dto.getParameterType())
                .operator(dto.getOperator())
                .value1(dto.getValue1())
                .value2(dto.getValue2())
                .build();
    }

    public static RuleDTO toDTO(SubscriptionRule rule) {
        return RuleDTO.builder()
                .id(rule.getId())
                .parameterType(rule.getParameterType())
                .operator(rule.getOperator())
                .value1(rule.getValue1())
                .value2(rule.getValue2())
                .build();
    }

}
