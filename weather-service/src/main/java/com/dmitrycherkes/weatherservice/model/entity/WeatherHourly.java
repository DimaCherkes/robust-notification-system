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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MonitoredCity city;

    @Column(name = "forecast_time", nullable = false)
    private OffsetDateTime forecastTime;

    private BigDecimal temp;

    @Column(name = "feels_like")
    private BigDecimal feelsLike;

    private Integer pressure;

    private Integer humidity;

    @Column(name = "dew_point")
    private BigDecimal dewPoint;

    private BigDecimal uvi;

    private Integer clouds;

    private Integer visibility;

    @Column(name = "wind_speed")
    private BigDecimal windSpeed;

    @Column(name = "wind_deg")
    private Integer windDeg;

    @Column(name = "wind_gust")
    private BigDecimal windGust;

    private BigDecimal pop;

    @Column(name = "weather_main")
    private String weatherMain;

    @Column(name = "weather_description")
    private String weatherDescription;

    @Column(name = "weather_icon")
    private String weatherIcon;
}
