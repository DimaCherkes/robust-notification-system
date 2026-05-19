package com.dmytrocherkes.iamservice.service;

import com.dmytrocherkes.iamservice.model.dto.sns.UserCreatedEventDTO;
import com.dmytrocherkes.iamservice.model.entity.User;
import com.dmytrocherkes.iamservice.model.enums.AwsMessageTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsPublisher {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${app.aws.sns.iam-topic-arn:}")
    private String iamTopicArn;


    public void publishUserChangesEventToSns(User persistedUser, AwsMessageTypes messageType) {
        UserCreatedEventDTO userDto = UserCreatedEventDTO.builder()
                .userId(persistedUser.getId())
                .email(persistedUser.getEmail())
                .username(persistedUser.getUsername())
                .build();
        publishMessage(iamTopicArn, userDto,
                Map.of(AwsMessageTypes.ACTION.getType(), messageType.getType())
        );
    }

    /**
     * Publish message with attributes for Filter Policy (SNS)
     *
     * @param topicArn   ARN of the SNS topic to which the message will be published
     * @param payload    Object which will be the message body (will be serialized to JSON)
     * @param attributes Map of attributes (for example, "action" -> "delete")
     */
    private void publishMessage(String topicArn, Object payload, Map<String, String> attributes) {
        try {
            String message = objectMapper.writeValueAsString(payload);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .messageAttributes(translateAttributes(attributes))
                    .build();

            log.info("Publishing message to topic: {} with attributes: {}", topicArn, attributes);
            snsClient.publish(request);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SNS message payload", e);
            throw new RuntimeException("SNS serialization error", e);
        } catch (Exception e) {
            log.error("Failed to publish message to SNS topic: {}", topicArn, e);
            throw e;
        }
    }

    // String -> MessageAttributeValue
    private Map<String, MessageAttributeValue> translateAttributes(Map<String, String> attributes) {
        if (attributes == null) return Map.of();

        return attributes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue(e.getValue())
                                .build()
                ));
    }

}
