package com.dmitrycherkes.weatherservice.listener;

import com.dmitrycherkes.weatherservice.model.dto.CityDTO;
import com.dmitrycherkes.weatherservice.service.MonitoredCityService;
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
public class SubscriptionListener {

    private final MonitoredCityService monitoredCityService;
    private final ObjectMapper objectMapper;

    @SqsListener("${app.sqs.weather-queue}")
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

            if (root == null) {
                root = objectMapper.readTree(rawPayload);
            }
            String messageContent = root.has("Message") ? root.get("Message").asText() : rawPayload;
            CityDTO cityDTO = objectMapper.readValue(messageContent, CityDTO.class);

            switch (action) {
                case "city_activate" -> monitoredCityService.activateCity(cityDTO);
                case "city_deactivate" -> monitoredCityService.deactivateCity(cityDTO);
                default -> log.warn("Unknown action type: {}", action);
            }

        } catch (Exception e) {
            log.error("Failed to parse or process message: {}", rawPayload, e);
        }
    }
}
