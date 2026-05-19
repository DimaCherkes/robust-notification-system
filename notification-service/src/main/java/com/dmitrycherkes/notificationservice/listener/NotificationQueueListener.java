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

    private void handleDeleteUser(String rawPayload) {
        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
            JsonNode messageNode = objectMapper.readTree(messageContent);
            
            Long userId = messageNode.has("userId") ? messageNode.get("userId").asLong() : messageNode.get("id").asLong();
            
            userRepository.deleteById(userId);
            log.info("User {} deleted successfully from notification-service", userId);
        } catch (Exception e) {
            log.error("Failed to process user_delete action", e);
        }
    }

    private void handleSendEmail(String rawPayload) {
        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
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

    private void handleUpsertUser(String rawPayload) {
        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
            JsonNode messageNode = objectMapper.readTree(messageContent);
            
            Long userId = messageNode.has("userId") ? messageNode.get("userId").asLong() : messageNode.get("id").asLong();
            String email = messageNode.get("email").asText();
            String username = messageNode.has("username") ? messageNode.get("username").asText() : null;

            User user = userRepository.findById(userId).orElse(new User());
            
            user.setId(userId);
            user.setEmail(email);
            user.setUsername(username);
            
            userRepository.save(user);
            log.info("User {} upserted successfully in notification-service", userId);
        } catch (Exception e) {
            log.error("Failed to process user_upsert action", e);
        }
    }
}