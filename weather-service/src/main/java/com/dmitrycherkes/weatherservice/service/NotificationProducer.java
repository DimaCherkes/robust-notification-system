package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.NotificationMessage;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final SqsTemplate sqsTemplate;

    @Value("${app.sqs.notification-queue}")
    private String notificationQueue;

    public void sendNotification(NotificationMessage message) {
        log.info("Sending notification message to SQS queue {}: {}", notificationQueue, message);
        sqsTemplate.send(notificationQueue, message);
    }
}
