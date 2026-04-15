package com.dmitrycherkes.decisionservice.model.entity;

import com.dmitrycherkes.decisionservice.model.enums.ParameterType;
import com.dmitrycherkes.decisionservice.model.enums.RuleOperator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

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
    private Integer id;

    @Column(name = "subscription_id", nullable = false)
    private Integer subscriptionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false, length = 50)
    private ParameterType parameterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 20)
    private RuleOperator operator;

    @Column(name = "value_1", nullable = false, precision = 10, scale = 2)
    private BigDecimal value1;

    @Column(name = "value_2", precision = 10, scale = 2)
    private BigDecimal value2;

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours;

    @UpdateTimestamp
    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt;
}
