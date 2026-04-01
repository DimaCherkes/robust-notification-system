package com.dmitrycherkes.weatherservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for parsing OpenWeatherMap One Call API response.
 * Uses @JsonIgnoreProperties(ignoreUnknown = true) to skip fields like 'minutely'.
 * Uses @JsonInclude(JsonInclude.Include.NON_NULL) for flexibility and safety.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherApiResponseDTO {

    private BigDecimal lat;
    private BigDecimal lon;
    private String timezone;
    
    @JsonProperty("timezone_offset")
    private Integer timezoneOffset;

    private CurrentWeatherDTO current;
    private List<HourlyWeatherDTO> hourly;
    private List<DailyWeatherDTO> daily;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CurrentWeatherDTO {
        private Long dt;
        private Long sunrise;
        private Long sunset;
        private BigDecimal temp;
        
        @JsonProperty("feels_like")
        private BigDecimal feelsLike;
        
        private Integer pressure;
        private Integer humidity;
        
        @JsonProperty("dew_point")
        private BigDecimal dewPoint;
        
        private BigDecimal uvi;
        private Integer clouds;
        private Integer visibility;
        
        @JsonProperty("wind_speed")
        private BigDecimal windSpeed;
        
        @JsonProperty("wind_deg")
        private Integer windDeg;
        
        private List<WeatherDescriptionDTO> weather;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HourlyWeatherDTO {
        private Long dt;
        private BigDecimal temp;
        
        @JsonProperty("feels_like")
        private BigDecimal feelsLike;
        
        private Integer pressure;
        private Integer humidity;
        
        @JsonProperty("dew_point")
        private BigDecimal dewPoint;
        
        private BigDecimal uvi;
        private Integer clouds;
        private Integer visibility;
        
        @JsonProperty("wind_speed")
        private BigDecimal windSpeed;
        
        @JsonProperty("wind_deg")
        private Integer windDeg;
        
        @JsonProperty("wind_gust")
        private BigDecimal windGust;
        
        private List<WeatherDescriptionDTO> weather;
        private BigDecimal pop; // Probability of precipitation
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DailyWeatherDTO {
        private Long dt;
        private Long sunrise;
        private Long sunset;
        private Long moonrise;
        private Long moonset;
        
        @JsonProperty("moon_phase")
        private BigDecimal moonPhase;
        
        private String summary;
        private TemperatureDTO temp;
        
        @JsonProperty("feels_like")
        private FeelsLikeDTO feelsLike;
        
        private Integer pressure;
        private Integer humidity;
        
        @JsonProperty("dew_point")
        private BigDecimal dewPoint;
        
        @JsonProperty("wind_speed")
        private BigDecimal windSpeed;
        
        @JsonProperty("wind_deg")
        private Integer windDeg;
        
        @JsonProperty("wind_gust")
        private BigDecimal windGust;
        
        private List<WeatherDescriptionDTO> weather;
        private Integer clouds;
        private BigDecimal pop;
        private BigDecimal uvi;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TemperatureDTO {
        private BigDecimal day;
        private BigDecimal min;
        private BigDecimal max;
        private BigDecimal night;
        private BigDecimal eve;
        private BigDecimal morn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FeelsLikeDTO {
        private BigDecimal day;
        private BigDecimal night;
        private BigDecimal eve;
        private BigDecimal morn;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WeatherDescriptionDTO {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }
}
