package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Integer> {
}
