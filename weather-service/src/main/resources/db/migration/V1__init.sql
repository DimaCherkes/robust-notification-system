-- Weather Service Database Schema
-- Focus: Weather Forecast Data and Local Rule Cache for Autonomy

-- 1. Monitored Cities (Synced from Subscription Service via SQS)
CREATE TABLE monitored_cities
(
    city_id       INT PRIMARY KEY, -- ID from Subscription Service
    name          VARCHAR(100),
    latitude      DECIMAL(9, 6) NOT NULL,
    longitude     DECIMAL(9, 6) NOT NULL,
    is_active     BOOLEAN DEFAULT true,
    last_api_call TIMESTAMP WITH TIME ZONE,
    timezone      VARCHAR(50)
);

-- 2. Hourly Forecast Data (Structured storage for 48h forecast)
CREATE TABLE weather_hourly
(
    id            BIGSERIAL PRIMARY KEY,
    city_id       INT                      NOT NULL REFERENCES monitored_cities (city_id) ON DELETE CASCADE,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- "dt" field from JSON
    temp          DECIMAL(5, 2),
    humidity      INT,
    wind_speed    DECIMAL(5, 2),
    pop           DECIMAL(3, 2),                     -- Probability of precipitation (0.0 - 1.0)
    weather_main  VARCHAR(50),                       -- "Rain", "Clouds", etc.

    UNIQUE (city_id, forecast_time)
);

-- 3. Active Rules Cache (Synced from Subscription Service via SQS)
-- Allows checking conditions without calling other services
CREATE TABLE active_rules_cache
(
    rule_id             UUID PRIMARY KEY,        -- Original ID from Subscription Service
    subscription_id     UUID           NOT NULL,
    city_id             INT            NOT NULL REFERENCES monitored_cities (city_id),
    user_id             BIGINT         NOT NULL, -- Needed for Notification Service event
    parameter_type      VARCHAR(20)    NOT NULL, -- TEMPERATURE, HUMIDITY, etc.
    operator            VARCHAR(10)    NOT NULL, -- GT, LT, BETWEEN
    value_1             DECIMAL(10, 2) NOT NULL,
    value_2             DECIMAL(10, 2),
    notify_before_hours INT DEFAULT 0
);

-- 4. Alert History (Prevent duplicate notifications/spam)
-- Resilience: If the system restarts, it won't re-send alerts for the same event
CREATE TABLE alert_history
(
    id            BIGSERIAL PRIMARY KEY,
    rule_id       UUID                     NOT NULL REFERENCES active_rules_cache (rule_id) ON DELETE CASCADE,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- The time point that triggered the alert
    sent_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (rule_id, forecast_time)                  -- Ensure one alert per rule per forecast hour
);

-- Index for the Condition Checker (Performance optimization)
CREATE INDEX idx_weather_lookup ON weather_hourly (city_id, forecast_time);