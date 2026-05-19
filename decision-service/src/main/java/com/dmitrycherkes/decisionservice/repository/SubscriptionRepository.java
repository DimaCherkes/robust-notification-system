package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT DISTINCT s.cityId FROM Subscription s")
    List<Integer> findDistinctCityIds();

    List<Subscription> findAllByCityId(Integer cityId);

    void deleteAllByUserId(Integer userId);
}
