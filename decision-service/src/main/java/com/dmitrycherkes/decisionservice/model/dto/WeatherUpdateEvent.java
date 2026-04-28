package com.dmitrycherkes.decisionservice.model.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WeatherUpdateEvent {
    private Integer cityId;
    private String cityName;
    private List<HourlyChange> changes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class HourlyChange {
        private OffsetDateTime forecastTime;
        private Map<String, FieldChange> fieldChanges;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class FieldChange {
        private Object oldValue;
        private Object newValue;
    }
}
