package com.dmitrycherkes.weatherservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionListener {

//    private final SubscriptionSyncService subscriptionSyncService;

//    @SqsListener("${app.sqs.subscription-queue}")
//    public void listen(SubscriptionMessage message) {
//        log.info("Received subscription message: {}", message);
//        try {
//            subscriptionSyncService.processSubscriptionMessage(message);
//        } catch (Exception e) {
//            log.error("Error processing subscription message", e);
//            // In a real system, you might want to throw exception to retry or move to DLQ
//            throw e;
//        }
//    }
}
