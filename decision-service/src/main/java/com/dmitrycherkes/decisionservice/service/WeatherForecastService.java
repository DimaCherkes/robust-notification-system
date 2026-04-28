package com.dmitrycherkes.decisionservice.service;

import com.dmitrycherkes.decisionservice.model.dto.WeatherUpdateEvent;
import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import com.dmitrycherkes.decisionservice.repository.WeatherForecastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherForecastService {

    private final WeatherForecastRepository weatherForecastRepository;

    @Transactional
    public void upsertWeatherForecast(WeatherUpdateEvent event) {
        log.info("Upserting weather forecast for city: {} (ID: {})", event.getCityName(), event.getCityId());
        
        for (WeatherUpdateEvent.HourlyChange change : event.getChanges()) {
            WeatherForecast forecast = weatherForecastRepository
                    .findByCityIdAndForecastTime(event.getCityId(), change.getForecastTime())
                    .orElse(WeatherForecast.builder()
                            .cityId(event.getCityId())
                            .forecastTime(change.getForecastTime())
                            .build());

            updateFields(forecast, change.getFieldChanges());
            weatherForecastRepository.save(forecast);
        }
    }

    private void updateFields(WeatherForecast forecast, Map<String, WeatherUpdateEvent.FieldChange> fieldChanges) {
        fieldChanges.forEach((field, change) -> {
            Object newValue = change.getNewValue();
            if (newValue == null) return;

            switch (field) {
                case "temp" -> forecast.setTemp(toBigDecimal(newValue));
                case "humidity" -> forecast.setHumidity(toInteger(newValue));
                case "windSpeed" -> forecast.setWindSpeed(toBigDecimal(newValue));
                case "pop" -> forecast.setPop(toBigDecimal(newValue));
                case "weatherMain" -> forecast.setWeatherMain(String.valueOf(newValue));
            }
        });
    }

    private BigDecimal toBigDecimal(Object val) {
        try {
            if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            log.warn("Could not convert value {} to BigDecimal", val);
            return null;
        }
    }

    private Integer toInteger(Object val) {
        try {
            if (val instanceof Number n) return n.intValue();
            return Integer.valueOf(String.valueOf(val));
        } catch (Exception e) {
            log.warn("Could not convert value {} to Integer", val);
            return null;
        }
    }
}
