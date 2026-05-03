package com.dmytrocherkes.subscriptionservice.listener;

import com.dmytrocherkes.subscriptionservice.service.SubscriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionDeactivationListener {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.subscription-consume-queue}")
    public void listen(@Payload String rawPayload,
                       @Header(value = "action", required = false) String sqsHeaderAction) {
        log.debug("Received message in subscription-consume-queue: {}", rawPayload);

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

            log.info("Processing subscription action: {}", action);

            switch (action) {
                case "subscription_deactivate" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
                    String idStr = messageContent.replace("\"", "").trim();
                    log.debug("Extracted UUID string: {}", idStr);
                    subscriptionService.deactivateAfterTrigger(UUID.fromString(idStr));
                }
                case "future potential event type" -> {
                    log.debug("This is future potential case");
                }
                default -> log.warn("Unknown action type: {}", action);
            }
        } catch (Exception e) {
            log.error("Failed to process message in subscription-consume-queue: {}", rawPayload, e);
        }
    }
}
