package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.SubscriptionRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRuleRepository extends JpaRepository<SubscriptionRule, Integer> {
}
