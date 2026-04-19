package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRuleRepository extends JpaRepository<SubscriptionRule, Integer> {

    List<SubscriptionRule> getAllByCityId(Integer cityId);

    List<Integer> getAllCityIds();

}
