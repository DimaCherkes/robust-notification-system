package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {

    boolean existsBySubscriptionIdAndForecastTime(UUID subscriptionId, OffsetDateTime forecastTime);

    Optional<AlertHistory> findFirstBySubscriptionIdOrderByTriggeredAtDesc(UUID subscriptionId);

}
