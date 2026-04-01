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

}
