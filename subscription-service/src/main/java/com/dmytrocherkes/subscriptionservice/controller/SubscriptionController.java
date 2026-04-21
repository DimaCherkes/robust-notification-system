package com.dmytrocherkes.subscriptionservice.controller;

import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;
import com.dmytrocherkes.subscriptionservice.service.SubscriptionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionServiceImpl subscriptionService;

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody SubscriptionRequest request) {
        log.trace(ApiLogMessage.REST_CREATE_SUBSCRIPTION.getValue(), request.getUserId());
        SubscriptionResponse response = subscriptionService.createSubscription(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable UUID id) {
        log.trace(ApiLogMessage.REST_GET_SUBSCRIPTION_BY_ID.getValue(), id);
        SubscriptionResponse response = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponse>> getAllByUserId(@PathVariable Integer userId) {
        log.trace(ApiLogMessage.REST_GET_SUBSCRIPTIONS_BY_USER.getValue(), userId);
        List<SubscriptionResponse> response = subscriptionService.getAllByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(@PathVariable UUID id,
                                                       @Valid @RequestBody SubscriptionRequest request) {
        log.trace(ApiLogMessage.REST_UPDATE_SUBSCRIPTION.getValue(), id);
        SubscriptionResponse response = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.trace(ApiLogMessage.REST_DELETE_SUBSCRIPTION.getValue(), id);
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
