package com.dmytrocherkes.subscriptionservice.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Double temperatureThreshold;

    @Column(nullable = false)
    private String condition; // ABOVE, BELOW

    private LocalDateTime lastNotifiedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
