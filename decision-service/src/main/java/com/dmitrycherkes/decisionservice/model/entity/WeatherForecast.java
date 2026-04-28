package com.dmitrycherkes.decisionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "weather_forecast")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    private BigDecimal temp;
    private Integer humidity;

    @Column(name = "wind_speed")
    private BigDecimal windSpeed;

    private BigDecimal pop;

    @Column(name = "weather_main")
    private String weatherMain;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
