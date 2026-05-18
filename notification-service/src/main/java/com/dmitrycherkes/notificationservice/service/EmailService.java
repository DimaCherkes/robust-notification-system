package com.dmitrycherkes.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final SesAsyncClient sesClient;

    @Value("${aws.ses.sender:noreply@bachelor-project.dmitry-cherkes.com}")
    private String sender;

    public CompletableFuture<SendEmailResponse> sendEmail(String toAddress, String subject, String body) {
        log.info("Preparing to send email to {}", toAddress);
        SendEmailRequest request = SendEmailRequest.builder()
                .source(sender)
                .destination(Destination.builder().toAddresses(toAddress).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).charset("UTF-8").build())
                        .body(Body.builder().text(Content.builder().data(body).charset("UTF-8").build()).build())
                        .build())
                .build();

        return sesClient.sendEmail(request)
                .whenComplete((response, error) -> {
                    if (error != null) {
                        log.error("Failed to send email to {}", toAddress, error);
                    } else {
                        log.info("Email sent to {}, MessageId: {}", toAddress, response.messageId());
                    }
                });
    }
}