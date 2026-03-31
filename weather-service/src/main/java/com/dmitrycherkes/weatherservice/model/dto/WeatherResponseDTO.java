package com.dmitrycherkes.weatherservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeatherResponseDTO {
    private List<HourlyForecastDTO> hourly;

    @Data
    public static class HourlyForecastDTO {
        private Long dt;
        private Integer temp;
        private Integer humidity;
        @JsonProperty("wind_speed")
        private Integer windSpeed;
        private Integer pop;
        private List<WeatherDescriptionDTO> weather;
    }

    @Data
    public static class WeatherDescriptionDTO {
        private String main;
    }
}
