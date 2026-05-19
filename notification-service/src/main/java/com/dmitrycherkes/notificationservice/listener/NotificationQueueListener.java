package com.dmitrycherkes.notificationservice.listener;

import com.dmitrycherkes.notificationservice.model.dto.EmailMessageDto;
import com.dmitrycherkes.notificationservice.model.dto.UserMessageDto;
import com.dmitrycherkes.notificationservice.model.entity.NotificationHistory;
import com.dmitrycherkes.notificationservice.model.entity.User;
import com.dmitrycherkes.notificationservice.model.enums.NotificationStatus;
import com.dmitrycherkes.notificationservice.repository.NotificationHistoryRepository;
import com.dmitrycherkes.notificationservice.repository.UserRepository;
import com.dmitrycherkes.notificationservice.service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;

    @SqsListener("${app.aws.sqs.notification-consume-queue}")
    public void listen(@Payload String rawPayload, 
                       @Header(value = "action", required = false) String sqsHeaderAction) {
        log.debug("Received raw payload: {}", rawPayload);

        try {
            String action = sqsHeaderAction;
            JsonNode root = null;

            if (action == null) {
                root = objectMapper.readTree(rawPayload);
                if (root.has("MessageAttributes") && root.get("MessageAttributes").has("action")) {
                    action = root.get("MessageAttributes").get("action").get("Value").asText();
                } else if (root.has("action")) {
                    action = root.get("action").asText();
                }
            }

            if (action == null) {
                log.warn("Could not find 'action' in message headers or payload");
                return;
            }

            log.info("Processing action: {}", action);

            switch (action) {
                case "sent_email" -> handleSendEmail(rawPayload);
                case "user_upsert" -> handleUpsertUser(rawPayload);
                case "user_delete" -> handleDeleteUser(rawPayload);
                default -> log.warn("Unknown action type: {}", action);
            }

        } catch (Exception e) {
            log.error("Failed to parse or process message: {}", rawPayload, e);
        }
    }

    private void handleDeleteUser(String messageContent) {
        // todo: impl removing user by id (should be configured cascade delete for related entities in sql V1__init.sql)
    }

    private void handleSendEmail(String messageContent) {
        try {
            EmailMessageDto emailDto = objectMapper.readValue(messageContent, EmailMessageDto.class);
            User user = userRepository.findById(emailDto.getUserId()).orElse(null);
            if (user == null) {
                log.warn("User not found: {}", emailDto.getUserId());
                return;
            }

            NotificationHistory history = NotificationHistory.builder()
                    .user(user)
                    .subscriptionId(emailDto.getSubscriptionId())
                    .recipientEmail(user.getEmail())
                    .subject(emailDto.getSubject())
                    .content(emailDto.getContent())
                    .status(NotificationStatus.PENDING)
                    .build();
            history = notificationHistoryRepository.save(history);

            final NotificationHistory finalHistory = history;
            emailService.sendEmail(user.getEmail(), emailDto.getSubject(), emailDto.getContent())
                    .whenComplete((response, error) -> {
                        if (error != null) {
                            finalHistory.setStatus(NotificationStatus.FAILED);
                            finalHistory.setErrorMessage(error.getMessage());
                        } else {
                            finalHistory.setStatus(NotificationStatus.SENT);
                        }
                        notificationHistoryRepository.save(finalHistory);
                    });

        } catch (Exception e) {
            log.error("Failed to process sent_email action", e);
        }
    }

    private void handleUpsertUser(String messageContent) {
        try {
            UserMessageDto userDto = objectMapper.readValue(messageContent, UserMessageDto.class);
            User user = userRepository.findById(userDto.getId())
                    .orElse(new User());
            
            if (user.getId() == null) {
                user.setId(userDto.getId());
            }
            user.setEmail(userDto.getEmail());
            user.setUsername(userDto.getUsername());
            
            userRepository.save(user);
            log.info("User {} upserted successfully", userDto.getId());
        } catch (Exception e) {
            log.error("Failed to process upsert_user action", e);
        }
    }
}