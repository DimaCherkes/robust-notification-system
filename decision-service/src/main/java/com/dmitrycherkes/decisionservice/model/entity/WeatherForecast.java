package com.dmitrycherkes.decisionservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "replica_weather_forecast", uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "forecast_time"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    @Column(precision = 5, scale = 2)
    private BigDecimal temp;

    private Integer humidity;

    @Column(name = "wind_speed", precision = 5, scale = 2)
    private BigDecimal windSpeed;

    @Column(precision = 3, scale = 2)
    private BigDecimal pop;

    @Column(name = "weather_main", length = 50)
    private String weatherMain;

    @CreationTimestamp
    @Column(name = "replicated_at")
    private OffsetDateTime replicatedAt;
}
