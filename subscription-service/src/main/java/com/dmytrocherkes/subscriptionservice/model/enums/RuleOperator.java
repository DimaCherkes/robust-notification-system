package com.dmytrocherkes.subscriptionservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RuleOperator {
    GREATER_THAN,
    LESS_THAN,
    EQUALS,
    BETWEEN;

    @JsonCreator
    public static RuleOperator fromString(String value) {
        if (value == null) return null;
        return switch (value.toUpperCase()) {
            case "GT", "GREATER_THAN" -> GREATER_THAN;
            case "LT", "LESS_THAN" -> LESS_THAN;
            case "EQ", "EQUALS" -> EQUALS;
            case "BETWEEN" -> BETWEEN;
            default -> throw new IllegalArgumentException("Unknown operator: " + value);
        };
    }
}
