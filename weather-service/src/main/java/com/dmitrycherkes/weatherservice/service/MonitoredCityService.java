package com.dmitrycherkes.weatherservice.service;

import com.dmitrycherkes.weatherservice.model.dto.CityDTO;
import com.dmitrycherkes.weatherservice.model.entity.MonitoredCity;
import com.dmitrycherkes.weatherservice.repository.MonitoredCityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoredCityService {

    private final MonitoredCityRepository monitoredCityRepository;

    @Transactional
    public void activateCity(CityDTO cityDTO) {
        log.info("Activating city: {}", cityDTO.getName());
        updateCityStatus(cityDTO, true);
    }

    @Transactional
    public void deactivateCity(CityDTO cityDTO) {
        log.info("Deactivating city: {}", cityDTO.getName());
        updateCityStatus(cityDTO, false);
    }

    private void updateCityStatus(CityDTO cityDTO, boolean isActive) {
        MonitoredCity city = monitoredCityRepository.findById(cityDTO.getId())
                .orElseGet(() -> MonitoredCity.builder()
                        .cityId(cityDTO.getId())
                        .name(cityDTO.getName())
                        .latitude(cityDTO.getLatitude())
                        .longitude(cityDTO.getLongitude())
                        .timezone(cityDTO.getTimezone())
                        .build());
        
        city.setIsActive(isActive);
        monitoredCityRepository.save(city);
        log.debug("City {} is now {}", cityDTO.getName(), isActive ? "active" : "inactive");
    }
}
