package com.dmitrycherkes.weatherservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherUpdateEvent {
    private Integer cityId;
    private String cityName;
    private List<HourlyChange> changes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyChange {
        private OffsetDateTime forecastTime;
        // Map of changes: field name -> {old value, new value}
        private Map<String, FieldChange> fieldChanges;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldChange {
        private Object oldValue;
        private Object newValue;
    }
}
