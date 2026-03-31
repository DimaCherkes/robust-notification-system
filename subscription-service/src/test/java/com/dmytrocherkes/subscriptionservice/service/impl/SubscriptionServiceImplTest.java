package com.dmytrocherkes.subscriptionservice.service.impl;

import com.dmytrocherkes.subscriptionservice.model.exception.ResourceNotFoundException;
import com.dmytrocherkes.subscriptionservice.mapper.SubscriptionMapper;
import com.dmytrocherkes.subscriptionservice.model.request.SubscriptionRequest;
import com.dmytrocherkes.subscriptionservice.model.response.SubscriptionResponse;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;
import com.dmytrocherkes.subscriptionservice.repository.CityRepository;
import com.dmytrocherkes.subscriptionservice.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private SubscriptionRequest request;
    private City city;
    private Subscription subscription;
    private SubscriptionResponse response;

    @BeforeEach
    void setUp() {
        request = SubscriptionRequest.builder()
                .userId(1L)
                .cityId(1)
                .notifyBeforeHours(1)
                .isActive(true)
                .build();

        city = City.builder()
                .id(1)
                .name("London")
                .build();

        subscription = Subscription.builder()
                .id(UUID.randomUUID())
                .userId(1L)
                .city(city)
                .notifyBeforeHours(1)
                .isActive(true)
                .build();

        response = SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(1L)
                .cityId(1)
                .cityName("London")
                .notifyBeforeHours(1)
                .isActive(true)
                .build();
    }

    @Test
    void createSubscription_Success() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(subscriptionMapper.toEntity(request)).thenReturn(subscription);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);
        when(subscriptionMapper.toResponse(subscription)).thenReturn(response);

        SubscriptionResponse result = subscriptionService.createSubscription(request);

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        verify(cityRepository).findById(1);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_CityNotFound_ThrowsException() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(request));
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void getSubscriptionById_Success() {
        UUID id = subscription.getId();
        when(subscriptionRepository.findById(id)).thenReturn(Optional.of(subscription));
        when(subscriptionMapper.toResponse(subscription)).thenReturn(response);

        SubscriptionResponse result = subscriptionService.getSubscriptionById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getSubscriptionById_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(subscriptionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.getSubscriptionById(id));
    }
}
