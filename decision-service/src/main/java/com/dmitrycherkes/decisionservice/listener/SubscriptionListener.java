package com.dmitrycherkes.decisionservice.listener;

import com.dmitrycherkes.decisionservice.model.dto.SubscriptionMessageDTO;
import com.dmitrycherkes.decisionservice.service.SubscriptionSyncService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionListener {

    private final SubscriptionSyncService subscriptionSyncService;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.subscription-queue-name}")
    public void listen(@Payload String rawPayload) {
        log.debug("Received raw payload: {}", rawPayload);

        try {
            JsonNode root = objectMapper.readTree(rawPayload);
            
            // 1. Extract Action from SNS MessageAttributes or root
            String action = null;
            if (root.has("MessageAttributes") && root.get("MessageAttributes").has("action")) {
                action = root.get("MessageAttributes").get("action").get("Value").asText();
            } else if (root.has("action")) {
                action = root.get("action").asText();
            }

            if (action == null) {
                log.warn("Could not find 'action' in message payload");
                return;
            }

            // 2. Extract Message content (SNS wraps it in 'Message' field as string)
            String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
            
            log.info("Processing action: {} for payload: {}", action, messageContent);

            switch (action) {
                case "subscription_created", "subscription_updated" -> {
                    SubscriptionMessageDTO dto = objectMapper.readValue(messageContent, SubscriptionMessageDTO.class);
                    subscriptionSyncService.upsertSubscription(dto);
                }
                case "subscription_deleted" -> {
                    String idStr = messageContent.replace("\"", "").trim();
                    subscriptionSyncService.deleteSubscription(UUID.fromString(idStr));
                }
                default -> log.warn("Unknown action type: {}", action);
            }

        } catch (Exception e) {
            log.error("Failed to parse or process message: {}", rawPayload, e);
        }
    }
}
