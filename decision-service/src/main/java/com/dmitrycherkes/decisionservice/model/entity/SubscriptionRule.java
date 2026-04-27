package com.dmitrycherkes.decisionservice.model.entity;

import com.dmitrycherkes.decisionservice.model.enums.ParameterType;
import com.dmitrycherkes.decisionservice.model.enums.RuleOperator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscription_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionRule {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false)
    private ParameterType parameterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator;

    @Column(name = "value_1", nullable = false)
    private BigDecimal value1;

    @Column(name = "value_2")
    private BigDecimal value2;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
