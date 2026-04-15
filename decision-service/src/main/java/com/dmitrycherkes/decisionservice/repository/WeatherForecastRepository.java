package com.dmitrycherkes.decisionservice.repository;

import com.dmitrycherkes.decisionservice.model.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Integer> {
}
