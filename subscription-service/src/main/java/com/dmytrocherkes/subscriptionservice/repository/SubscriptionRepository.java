package com.dmytrocherkes.subscriptionservice.repository;

import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findAllByUserId(Long userId);
}
