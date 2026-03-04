package com.dmytrocherkes.subscriptionservice.controller;

import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionRequestDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.repository.SubscriptionRepository;
import com.dmytrocherkes.subscriptionservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final JwtTokenProvider jwtTokenProvider;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(Authentication authentication,
                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("email", authentication.getName());
        response.put("roles", authentication.getAuthorities());
        
        List<Subscription> mySubscriptions = subscriptionRepository.findAllByUserId(userId);
        response.put("subscriptions", mySubscriptions);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody SubscriptionRequestDTO request,
                                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setPlanName(request.getPlanName());
        subscription.setStatus("ACTIVE");
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setExpiresAt(LocalDateTime.now().plusDays(30));

        Subscription saved = subscriptionRepository.save(subscription);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getAllMySubscriptions(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserId(token);
        
        List<Subscription> subscriptions = subscriptionRepository.findAllByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }
}
