package com.dmitrycherkes.decisionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours;

    @UpdateTimestamp
    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubscriptionRule> rules = new ArrayList<>();
}
