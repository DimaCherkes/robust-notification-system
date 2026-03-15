package com.dmytrocherkes.subscriptionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    // add enum
    @Column(name = "parameter_type", nullable = false)
    private String parameterType;

    // todo: add enum for operator
    @Column(nullable = false)
    private String operator;

    // todo: here can be just Integer
    @Column(name = "value_1", nullable = false)
    private BigDecimal value1;

    @Column(name = "value_2")
    private BigDecimal value2;
}
