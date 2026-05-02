package com.dmytrocherkes.subscriptionservice.controller;

import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionDTO;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.security.SecurityUtils;
import com.dmytrocherkes.subscriptionservice.service.SubscriptionService;
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

    private final SubscriptionService subscriptionService;

    @PostMapping("/create")
    public ResponseEntity<SubscriptionDTO> create(@Valid @RequestBody SubscriptionRequest request) {
        log.trace(ApiLogMessage.REST_CREATE_SUBSCRIPTION.getValue(), request.getUserId());
        SubscriptionDTO response = subscriptionService.createSubscription(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> getById(@PathVariable UUID id) {
        log.trace(ApiLogMessage.REST_GET_SUBSCRIPTION_BY_ID.getValue(), id);
        SubscriptionDTO response = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubscriptionDTO>> getAllForCurrentUser() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        log.trace(ApiLogMessage.REST_GET_SUBSCRIPTIONS_BY_USER.getValue(), currentUserEmail);
        List<SubscriptionDTO> response = subscriptionService.getAllForCurrentUser();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> update(@PathVariable UUID id,
                                                  @Valid @RequestBody SubscriptionRequest request) {
        log.trace(ApiLogMessage.REST_UPDATE_SUBSCRIPTION.getValue(), id);
        SubscriptionDTO response = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/soft/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable UUID id) {
        log.trace(ApiLogMessage.REST_SOFT_DELETE_SUBSCRIPTION.getValue(), id);
        subscriptionService.softDeleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<Void> hardDelete(@PathVariable UUID id) {
        log.trace(ApiLogMessage.REST_HARD_DELETE_SUBSCRIPTION.getValue(), id);
        subscriptionService.hardDeleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
