package com.dmytrocherkes.subscriptionservice.model.dto;

import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    private String city;
    private Double temperatureThreshold;
    private String condition; // ABOVE, BELOW
}
