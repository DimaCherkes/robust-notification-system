package com.dmytrocherkes.subscriptionservice.model.entity;

import com.dmytrocherkes.subscriptionservice.model.enums.ParameterType;
import com.dmytrocherkes.subscriptionservice.model.enums.RuleOperator;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private Integer value1;

    @Column(name = "value_2")
    private Integer value2;
}
