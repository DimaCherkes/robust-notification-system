package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.WeatherResponseDTO;
import com.dmitrycherkes.weatherservice.model.entity.MonitoredCity;
import com.dmitrycherkes.weatherservice.model.entity.WeatherHourly;
import com.dmitrycherkes.weatherservice.repository.MonitoredCityRepository;
import com.dmitrycherkes.weatherservice.repository.WeatherHourlyRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherSyncService {

    private final MonitoredCityRepository monitoredCityRepository;
    private final WeatherHourlyRepository weatherHourlyRepository;
    private final ConditionCheckerService conditionCheckerService;
    private final RestClient restClient;

    @Value("${app.weather.api-key}")
    private String apiKey;

    @Value("${app.weather.base-url}")
    private String baseUrl;

    @Transactional
    public void syncAllCities() {
        log.info("Starting weather synchronization for all active cities");
        List<MonitoredCity> activeCities = monitoredCityRepository.findAllByIsActiveTrue();
        log.info("Found {} active cities to sync", activeCities.size());

        for (MonitoredCity city : activeCities) {
            try {
                syncCityWeather(city);
            } catch (Exception e) {
                log.error("Failed to sync weather for city: {}", city.getName(), e);
            }
        }
        
        log.info("Weather synchronization completed, triggering condition checker");
        conditionCheckerService.checkAllConditions();
    }

    @CircuitBreaker(name = "weatherApi", fallbackMethod = "syncCityFallback")
    public void syncCityWeather(MonitoredCity city) {
        log.info("Fetching weather for city: {}", city.getName());

        WeatherResponseDTO response = restClient.get()
                .uri(baseUrl + "?lat={lat}&lon={lon}&exclude=current,minutely,daily,alerts&appid={appid}&units=metric",
                        city.getLatitude(), city.getLongitude(), apiKey)
                .retrieve()
                .body(WeatherResponseDTO.class);

        if (response != null && response.getHourly() != null) {
            log.info("Received {} hourly forecast records for city: {}", response.getHourly().size(), city.getName());
            upsertHourlyForecasts(city, response.getHourly());
            city.setLastApiCall(OffsetDateTime.now());
            monitoredCityRepository.save(city);
        }
    }

    private void upsertHourlyForecasts(MonitoredCity city, List<WeatherResponseDTO.HourlyForecastDTO> hourlyData) {
        for (WeatherResponseDTO.HourlyForecastDTO dto : hourlyData) {
            OffsetDateTime forecastTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(dto.getDt()), ZoneOffset.UTC);
            
            WeatherHourly weather = weatherHourlyRepository.findByCityCityIdAndForecastTime(city.getCityId(), forecastTime)
                    .orElse(WeatherHourly.builder()
                            .city(city)
                            .forecastTime(forecastTime)
                            .build());

            weather.setTemp(dto.getTemp());
            weather.setHumidity(dto.getHumidity());
            weather.setWindSpeed(dto.getWindSpeed());
            weather.setPop(dto.getPop());
            if (dto.getWeather() != null && !dto.getWeather().isEmpty()) {
                weather.setWeatherMain(dto.getWeather().getFirst().getMain());
            }

            weatherHourlyRepository.save(weather);
        }
    }

    public void syncCityFallback(MonitoredCity city, Throwable t) {
        log.error("Circuit breaker triggered for city {}: {}", city.getName(), t.getMessage());
    }
}
