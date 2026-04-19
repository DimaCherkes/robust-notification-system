package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Integer> {

    List<WeatherForecast> getAllByCityIdAndForecastTimeIsGreaterThan(Integer cityId, OffsetDateTime from);

}
