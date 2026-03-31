package com.dmitrycherkes.weatherservice.repository;

import com.dmitrycherkes.weatherservice.model.entity.MonitoredCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoredCityRepository extends JpaRepository<MonitoredCity, Integer> {
    List<MonitoredCity> findAllByIsActiveTrue();
}
