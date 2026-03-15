package com.dmytrocherkes.subscriptionservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ParameterType {
    TEMPERATURE,
    HUMIDITY,
    RAIN,
    WIND_SPEED;

    @JsonCreator
    public static ParameterType fromString(String value) {
        if (value == null) return null;
        try {
            return ParameterType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown parameter type: " + value);
        }
    }
}
