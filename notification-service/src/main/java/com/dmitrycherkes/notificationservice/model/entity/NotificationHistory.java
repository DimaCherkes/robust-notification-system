package com.dmitrycherkes.notificationservice.model.entity;

import com.dmitrycherkes.notificationservice.model.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subscription_id", nullable = false)
    private UUID subscriptionId;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @PrePersist
    protected void onSend() {
        if (sentAt == null) {
            sentAt = OffsetDateTime.now();
        }
    }
}
