package com.dmitrycherkes.weatherservice.repository;

import com.dmitrycherkes.weatherservice.model.entity.ActiveRuleCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActiveRuleCacheRepository extends JpaRepository<ActiveRuleCache, UUID> {
    List<ActiveRuleCache> findAllByCityCityId(Integer cityId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM ActiveRuleCache a WHERE a.subscriptionId = :subscriptionId")
    void deleteBySubscriptionId(java.util.UUID subscriptionId);
}
