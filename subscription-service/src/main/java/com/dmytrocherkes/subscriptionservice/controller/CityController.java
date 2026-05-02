package com.dmytrocherkes.subscriptionservice.controller;

import com.dmytrocherkes.subscriptionservice.model.constants.ApiLogMessage;
import com.dmytrocherkes.subscriptionservice.model.dto.CityDTO;
import com.dmytrocherkes.subscriptionservice.security.SecurityUtils;
import com.dmytrocherkes.subscriptionservice.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("/all")
    public ResponseEntity<List<CityDTO>> getAllForCurrentUser() {
        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        log.trace(ApiLogMessage.REST_GET_SUBSCRIPTIONS_BY_USER.getValue(), currentUserEmail);
        List<CityDTO> response = cityService.findAllCities();
        return ResponseEntity.ok(response);
    }

}
