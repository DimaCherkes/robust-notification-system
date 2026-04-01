package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.WeatherApiResponseDTO;
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
    }

    @CircuitBreaker(name = "weatherApi", fallbackMethod = "syncCityFallback")
    public void syncCityWeather(MonitoredCity city) {
        log.info("Fetching weather for city: {}", city.getName());
        log.info("API key: {} ", apiKey);

        WeatherApiResponseDTO response = restClient.get()
                .uri(baseUrl + "?lat={lat}&lon={lon}&appid={appid}",
                        city.getLatitude(), city.getLongitude(), apiKey)
                .retrieve()
                .body(WeatherApiResponseDTO.class);

        if (response != null && response.getHourly() != null) {
            log.info("Received {} hourly forecast records for city: {}", response.getHourly().size(), city.getName());
            updateHourlyForecasts(city, response.getHourly());
            city.setLastApiCall(OffsetDateTime.now());
            monitoredCityRepository.save(city);
        }
    }

    private void updateHourlyForecasts(MonitoredCity city, List<WeatherApiResponseDTO.HourlyWeatherDTO> hourlyData) {
        for (WeatherApiResponseDTO.HourlyWeatherDTO dto : hourlyData) {
            OffsetDateTime forecastTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(dto.getDt()), ZoneOffset.UTC);
            
            WeatherHourly weather = weatherHourlyRepository.findByCityCityIdAndForecastTime(city.getCityId(), forecastTime)
                    .orElse(WeatherHourly.builder()
                            .city(city)
                            .forecastTime(forecastTime)
                            .build());

            weather.setTemp(dto.getTemp());
            weather.setFeelsLike(dto.getFeelsLike());
            weather.setPressure(dto.getPressure());
            weather.setHumidity(dto.getHumidity());
            weather.setDewPoint(dto.getDewPoint());
            weather.setUvi(dto.getUvi());
            weather.setClouds(dto.getClouds());
            weather.setVisibility(dto.getVisibility());
            weather.setWindSpeed(dto.getWindSpeed());
            weather.setWindDeg(dto.getWindDeg());
            weather.setWindGust(dto.getWindGust());
            weather.setPop(dto.getPop());
            
            if (dto.getWeather() != null && !dto.getWeather().isEmpty()) {
                var weatherDesc = dto.getWeather().getFirst();
                weather.setWeatherMain(weatherDesc.getMain());
                weather.setWeatherDescription(weatherDesc.getDescription());
                weather.setWeatherIcon(weatherDesc.getIcon());
            }

            weatherHourlyRepository.save(weather);
        }
    }

    public void syncCityFallback(MonitoredCity city, Throwable t) {
        log.error("Circuit breaker triggered for city {}: {}", city.getName(), t.getMessage());
    }
}
