package com.dmytrocherkes.subscriptionservice.controller;

import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionRequestDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.security.JwtTokenProvider;
import com.dmytrocherkes.subscriptionservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final JwtTokenProvider jwtTokenProvider;
    private final SubscriptionService subscriptionService;

    @PostMapping("/create")
    public ResponseEntity<Subscription> createSubscription(@RequestBody SubscriptionRequestDTO request,
                                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        Subscription saved = subscriptionService.createSubscription(userId, request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllMySubscriptions(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        List<Subscription> subscriptions = subscriptionService.getAllSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscription(@PathVariable Long id,
                                                        @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        Subscription subscription = subscriptionService.getSubscriptionById(id);
        if (!subscription.getUserId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(subscription);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable Long id,
                                                           @RequestBody SubscriptionRequestDTO request,
                                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        Subscription updated = subscriptionService.updateSubscription(id, userId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        subscriptionService.deleteSubscription(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtTokenProvider.getUserId(token);
    }
}
