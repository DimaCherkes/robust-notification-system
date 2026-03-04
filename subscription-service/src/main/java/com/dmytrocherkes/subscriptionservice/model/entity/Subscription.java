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
    private Long userId; // Тот самый ID из IAM

    @Column(nullable = false)
    private String planName; // Например "PREMIUM", "FREE"

    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, CANCELLED

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
}
