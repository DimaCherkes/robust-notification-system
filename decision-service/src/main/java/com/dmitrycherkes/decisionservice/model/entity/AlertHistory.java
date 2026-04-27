package com.dmitrycherkes.decisionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "alert_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    @CreationTimestamp
    @Column(name = "triggered_at")
    private OffsetDateTime triggeredAt;
}
