package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import java.util.Optional;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Integer> {

    List<WeatherForecast> findAllByCityIdAndForecastTimeAfter(Integer cityId, OffsetDateTime from);

    Optional<WeatherForecast> findByCityIdAndForecastTime(Integer cityId, OffsetDateTime forecastTime);

}
