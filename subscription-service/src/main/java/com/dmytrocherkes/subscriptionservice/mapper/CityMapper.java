package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.dto.CityDTO;
import com.dmytrocherkes.subscriptionservice.model.dto.SubscriptionDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.City;
import com.dmytrocherkes.subscriptionservice.model.entity.Subscription;

import java.util.List;

public class CityMapper {

    public static CityDTO toDTO(City entity) {
        return CityDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .timezone(entity.getTimezone())
                .build();
    }

    public static List<CityDTO> toResponseList(List<City> entities) {
        return entities.stream()
                .map(CityMapper::toDTO)
                .toList();
    }
}
