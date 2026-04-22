package com.dmytrocherkes.subscriptionservice.model.dto;

import com.dmytrocherkes.subscriptionservice.model.enums.CityStatus;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// using for sns communication with weather microservice
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDTO {
    private Integer id;

    @NotNull(message = "City name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;

    @NotNull(message = "Timezone is required")
    private String timezone;

    @NotNull(message = "City status is required")
    private CityStatus status;
}
