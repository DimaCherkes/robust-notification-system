package com.dmytrocherkes.subscriptionservice.repository;

import com.dmytrocherkes.subscriptionservice.model.entity.SubscriptionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRuleRepository extends JpaRepository<SubscriptionRule, UUID> {
}
