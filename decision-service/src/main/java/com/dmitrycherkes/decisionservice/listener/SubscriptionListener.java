package com.dmitrycherkes.decisionservice.listener;

import com.dmitrycherkes.decisionservice.model.dto.SubscriptionMessageDTO;
import com.dmitrycherkes.decisionservice.model.dto.WeatherUpdateEvent;
import com.dmitrycherkes.decisionservice.service.SubscriptionSyncService;
import com.dmitrycherkes.decisionservice.service.WeatherForecastService;
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
public class SubscriptionListener {

    private final SubscriptionSyncService subscriptionSyncService;
    private final WeatherForecastService weatherForecastService;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.aws.sqs.decision-consume-queue}")
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
                case "subscription_created", "subscription_updated" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
                    SubscriptionMessageDTO dto = objectMapper.readValue(messageContent, SubscriptionMessageDTO.class);
                    subscriptionSyncService.upsertSubscription(dto);
                }
                case "subscription_deleted" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
                    String idStr = messageContent.replace("\"", "").trim();
                    subscriptionSyncService.deleteSubscription(UUID.fromString(idStr));
                }
                case "weather_update" -> {
                    if (root == null) root = objectMapper.readTree(rawPayload);
                    String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
                    WeatherUpdateEvent event = objectMapper.readValue(messageContent, WeatherUpdateEvent.class);
                    weatherForecastService.upsertWeatherForecast(event);
                }
                default -> log.warn("Unknown action type: {}", action);
            }

        } catch (Exception e) {
            log.error("Failed to parse or process message: {}", rawPayload, e);
        }
    }
}
