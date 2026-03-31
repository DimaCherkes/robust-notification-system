package com.dmitrycherkes.weatherservice.repository;

import com.dmitrycherkes.weatherservice.model.entity.WeatherHourly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherHourlyRepository extends JpaRepository<WeatherHourly, Long> {

    Optional<WeatherHourly> findByCityCityIdAndForecastTime(Integer cityId, OffsetDateTime forecastTime);

    @Query("SELECT w FROM WeatherHourly w WHERE w.city.cityId = :cityId AND w.forecastTime >= :targetTime ORDER BY w.forecastTime ASC")
    List<WeatherHourly> findClosestForecasts(Integer cityId, OffsetDateTime targetTime, org.springframework.data.domain.Pageable pageable);

    default Optional<WeatherHourly> findClosestForecast(Integer cityId, OffsetDateTime targetTime) {
        List<WeatherHourly> results = findClosestForecasts(cityId, targetTime, org.springframework.data.domain.PageRequest.of(0, 1));
        return results.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(results.get(0));
    }
}
