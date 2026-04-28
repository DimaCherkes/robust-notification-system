package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.WeatherApiResponseDTO;
import com.dmitrycherkes.weatherservice.model.dto.WeatherUpdateEvent;
import com.dmitrycherkes.weatherservice.model.entity.MonitoredCity;
import com.dmitrycherkes.weatherservice.model.entity.WeatherHourly;
import com.dmitrycherkes.weatherservice.repository.MonitoredCityRepository;
import com.dmitrycherkes.weatherservice.repository.WeatherHourlyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherSyncService {

    private final MonitoredCityRepository monitoredCityRepository;
    private final WeatherHourlyRepository weatherHourlyRepository;
    private final RestClient restClient;
    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.weather.api-key}")
    private String apiKey;

    @Value("${app.weather.base-url}")
    private String baseUrl;

    @Value("${app.sqs.decision-queue}")
    private String decisionQueue;

    @Transactional
    public List<WeatherUpdateEvent> syncAllCities() {
        log.info("Starting weather synchronization for all active cities");
        List<MonitoredCity> activeCities = monitoredCityRepository.findAllByIsActiveTrue();
        List<WeatherUpdateEvent> events = new ArrayList<>();

        for (MonitoredCity city : activeCities) {
            try {
                if (city.getLastApiCall() == null || 
                    OffsetDateTime.now().minusMinutes(15).isAfter(city.getLastApiCall())) {
                    
                    WeatherUpdateEvent event = syncCityWeather(city);
                    if (event != null && !event.getChanges().isEmpty()) {
                        events.add(event);
                        sendEventToQueue(event);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to sync weather for city: {}", city.getName(), e);
            }
        }
        
        log.info("Weather synchronization completed. Generated {} update events", events.size());
        return events;
    }

    private void sendEventToQueue(WeatherUpdateEvent event) {
        log.info("Sending weather update event to queue {}: {}", decisionQueue, event.getCityName());
        try {
            String payload = objectMapper.writeValueAsString(event);
            sqsTemplate.send(to -> to
                    .queue(decisionQueue)
                    .payload(payload)
                    .header("action", "weather_update")
            );
        } catch (Exception e) {
            log.error("Failed to serialize weather update event", e);
        }
    }

    @CircuitBreaker(name = "weatherApi", fallbackMethod = "syncCityFallback")
    public WeatherUpdateEvent syncCityWeather(MonitoredCity city) {
        log.info("Fetching weather for city: {}", city.getName());

        WeatherApiResponseDTO response = restClient.get()
                .uri(baseUrl + "?lat={lat}&lon={lon}&appid={appid}&units=metric",
                        city.getLatitude(), city.getLongitude(), apiKey)
                .retrieve()
                .body(WeatherApiResponseDTO.class);

        if (response != null && response.getHourly() != null) {
            List<WeatherUpdateEvent.HourlyChange> changes = updateHourlyForecasts(city, response.getHourly());
            
            city.setLastApiCall(OffsetDateTime.now());
            monitoredCityRepository.save(city);

            return WeatherUpdateEvent.builder()
                    .cityId(city.getCityId())
                    .cityName(city.getName())
                    .changes(changes)
                    .build();
        }
        return null;
    }

    private List<WeatherUpdateEvent.HourlyChange> updateHourlyForecasts(MonitoredCity city, List<WeatherApiResponseDTO.HourlyWeatherDTO> hourlyData) {
        List<WeatherUpdateEvent.HourlyChange> changes = new ArrayList<>();

        for (WeatherApiResponseDTO.HourlyWeatherDTO dto : hourlyData) {
            OffsetDateTime forecastTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(dto.getDt()), ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.HOURS);
            
            WeatherHourly weather = weatherHourlyRepository.findByCityCityIdAndForecastTime(city.getCityId(), forecastTime)
                    .orElse(WeatherHourly.builder()
                            .city(city)
                            .forecastTime(forecastTime)
                            .build());

            Map<String, WeatherUpdateEvent.FieldChange> fieldChanges = new HashMap<>();
            
            // Сравнение с округлением для BigDecimal
            compareAndSetRounded(fieldChanges, "temp", weather.getTemp(), dto.getTemp(), weather::setTemp);
            compareAndSetRounded(fieldChanges, "feelsLike", weather.getFeelsLike(), dto.getFeelsLike(), weather::setFeelsLike);
            compareAndSetRounded(fieldChanges, "dewPoint", weather.getDewPoint(), dto.getDewPoint(), weather::setDewPoint);
            compareAndSetRounded(fieldChanges, "uvi", weather.getUvi(), dto.getUvi(), weather::setUvi);
            compareAndSetRounded(fieldChanges, "windSpeed", weather.getWindSpeed(), dto.getWindSpeed(), weather::setWindSpeed);
            compareAndSetRounded(fieldChanges, "windGust", weather.getWindGust(), dto.getWindGust(), weather::setWindGust);
            compareAndSetRounded(fieldChanges, "pop", weather.getPop(), dto.getPop(), weather::setPop);

            // Сравнение обычных полей (Integer и String)
            compareAndSet(fieldChanges, "pressure", weather.getPressure(), dto.getPressure(), weather::setPressure);
            compareAndSet(fieldChanges, "humidity", weather.getHumidity(), dto.getHumidity(), weather::setHumidity);
            compareAndSet(fieldChanges, "clouds", weather.getClouds(), dto.getClouds(), weather::setClouds);
            compareAndSet(fieldChanges, "visibility", weather.getVisibility(), dto.getVisibility(), weather::setVisibility);
            compareAndSet(fieldChanges, "windDeg", weather.getWindDeg(), dto.getWindDeg(), weather::setWindDeg);

            if (dto.getWeather() != null && !dto.getWeather().isEmpty()) {
                var weatherDesc = dto.getWeather().getFirst();
                compareAndSet(fieldChanges, "weatherMain", weather.getWeatherMain(), weatherDesc.getMain(), weather::setWeatherMain);
                compareAndSet(fieldChanges, "weatherDescription", weather.getWeatherDescription(), weatherDesc.getDescription(), weather::setWeatherDescription);
                compareAndSet(fieldChanges, "weatherIcon", weather.getWeatherIcon(), weatherDesc.getIcon(), weather::setWeatherIcon);
            }

            if (!fieldChanges.isEmpty()) {
                weatherHourlyRepository.save(weather);
                changes.add(WeatherUpdateEvent.HourlyChange.builder()
                        .forecastTime(forecastTime)
                        .fieldChanges(fieldChanges)
                        .build());
            }
        }
        return changes;
    }

    /**
     * Сравнивает BigDecimal значения, округляя их до ближайшего целого.
     * Если целые числа различаются, сохраняет новое точное значение.
     */
    private void compareAndSetRounded(Map<String, WeatherUpdateEvent.FieldChange> changes, 
                                     String fieldName, BigDecimal oldValue, BigDecimal newValue, 
                                     Consumer<BigDecimal> setter) {
        if (newValue == null) return;
        
        boolean isDifferent = false;
        if (oldValue == null) {
            isDifferent = true;
        } else {
            long oldRounded = oldValue.setScale(0, RoundingMode.HALF_UP).longValue();
            long newRounded = newValue.setScale(0, RoundingMode.HALF_UP).longValue();
            if (oldRounded != newRounded) {
                isDifferent = true;
            }
        }

        if (isDifferent) {
            changes.put(fieldName, new WeatherUpdateEvent.FieldChange(oldValue, newValue));
            setter.accept(newValue);
        }
    }

    private <T> void compareAndSet(Map<String, WeatherUpdateEvent.FieldChange> changes, 
                                   String fieldName, T oldValue, T newValue, 
                                   Consumer<T> setter) {
        if (newValue != null && !Objects.equals(oldValue, newValue)) {
            changes.put(fieldName, new WeatherUpdateEvent.FieldChange(oldValue, newValue));
            setter.accept(newValue);
        }
    }

    public WeatherUpdateEvent syncCityFallback(MonitoredCity city, Throwable t) {
        log.error("Circuit breaker triggered for city {}: {}", city.getName(), t.getMessage());
        return null;
    }
}
