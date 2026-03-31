package com.dmitrycherkes.weatherservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather_hourly", uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "forecast_time"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherHourly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MonitoredCity city;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    private Integer temp;

    private Integer humidity;

    @Column(name = "wind_speed")
    private Integer windSpeed;

    private Integer pop;

    @Column(name = "weather_main")
    private String weatherMain;
}
