package com.dmitrycherkes.decisionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private SubscriptionRule rule;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    @CreationTimestamp
    @Column(name = "triggered_at")
    private OffsetDateTime triggeredAt;
}
