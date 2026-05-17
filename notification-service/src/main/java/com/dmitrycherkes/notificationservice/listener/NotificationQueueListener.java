package com.dmitrycherkes.notificationservice.listener;

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

    @SqsListener("${app.aws.sqs.notification-consume-queue}")
    public void listen(@Payload String rawPayload, 
                       @Header(value = "action", required = false) String sqsHeaderAction) {
        log.debug("Received raw payload: {}", rawPayload);

        try {
            // 1. Determine the action (either from SQS header or from SNS JSON body)
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

            // 2. Handle different actions
            switch (action) {
                case "sent_email" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;

                }
                case "upsert_user" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
                }
                default -> log.warn("Unknown action type: {}", action);
            }

        } catch (Exception e) {
            log.error("Failed to parse or process message: {}", rawPayload, e);
        }
    }
}
