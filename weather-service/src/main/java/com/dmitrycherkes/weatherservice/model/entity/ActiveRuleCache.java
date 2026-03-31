package com.dmitrycherkes.weatherservice.model.entity;

import com.dmitrycherkes.weatherservice.model.enums.ParameterType;
import com.dmitrycherkes.weatherservice.model.enums.RuleOperator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "active_rules_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveRuleCache {

    @Id
    @Column(name = "rule_id")
    private UUID ruleId;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MonitoredCity city;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "parameter_type", nullable = false)
    private ParameterType parameterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator;

    @Column(name = "value_1", nullable = false)
    private Integer value1;

    @Column(name = "value_2")
    private Integer value2;

    @Column(name = "notify_before_hours")
    @Builder.Default
    private Integer notifyBeforeHours = 0;
}
