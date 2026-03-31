package com.dmitrycherkes.weatherservice.repository;

import com.dmitrycherkes.weatherservice.model.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    boolean existsByRuleRuleIdAndForecastTime(UUID ruleId, OffsetDateTime forecastTime);
}
