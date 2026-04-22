package com.dmytrocherkes.subscriptionservice.mapper;

import com.dmytrocherkes.subscriptionservice.model.dto.CityDTO;
import com.dmytrocherkes.subscriptionservice.model.entity.City;

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
}
