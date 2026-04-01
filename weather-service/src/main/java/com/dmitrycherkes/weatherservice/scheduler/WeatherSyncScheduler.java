package com.dmitrycherkes.weatherservice.scheduler;

import com.dmitrycherkes.weatherservice.service.WeatherSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherSyncScheduler {

    private final WeatherSyncService weatherSyncService;

    @Scheduled(fixedDelayString = "30000")
    public void scheduleSync() {
        log.info("Scheduled weather synchronization started");
        weatherSyncService.syncAllCities();
    }
}
