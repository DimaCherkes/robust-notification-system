-- Decision Service Database Schema

-- 1. Replica of rules from Subscription Service
-- We store everything necessary, including userId, to avoid unnecessary synchronous calls to IAM Service
CREATE TABLE subscription_rules
(
    rule_id             UUID PRIMARY KEY,                   -- Original ID from Subscription Service
    subscription_id     UUID           NOT NULL,
    user_id             BIGINT         NOT NULL,            -- Replicated to construct the notification command
    city_id             INT            NOT NULL,
    parameter_type      VARCHAR(50)    NOT NULL,            -- e.g., TEMPERATURE, RAIN, WIND_SPEED
    operator            VARCHAR(20)    NOT NULL,            -- e.g., GREATER_THAN, LESS_THAN, BETWEEN
    value_1             DECIMAL(10, 2) NOT NULL,
    value_2             DECIMAL(10, 2),                     -- Used only for the 'BETWEEN' operator
    notify_before_hours INT                      DEFAULT 0, -- How many hours before the predicted event to notify
    last_synced_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Replica of weather forecast data from Weather Service
-- Optimized for fast lookups by city and forecast timestamp
CREATE TABLE weather_forecast
(
    id            BIGSERIAL PRIMARY KEY,
    city_id       INT                      NOT NULL,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- Specific forecast timestamp (Unix dt)
    temp          DECIMAL(5, 2),
    humidity      INT,
    wind_speed    DECIMAL(5, 2),
    pop           DECIMAL(3, 2),                     -- Probability of Precipitation (0.0 - 1.0)
    weather_main  VARCHAR(50),                       -- e.g., Rain, Snow, Clouds
    replicated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (city_id, forecast_time)                  -- Ensures only one snapshot per city per hour
);

-- 3. Alert History (Critical for Idempotency)
-- Prevents spamming: "One notification per rule per specific forecast hour"
CREATE TABLE alert_history
(
    id            BIGSERIAL PRIMARY KEY,
    rule_id       UUID                     NOT NULL REFERENCES subscription_rules (rule_id) ON DELETE CASCADE,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- The forecast timestamp that triggered the alert
    triggered_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (rule_id, forecast_time)                  -- Unique key to block duplicate alerts
);

-- 4. Performance Indexes
-- Needed for the Decision Engine to quickly find rules applicable to a specific city
CREATE INDEX idx_rules_city_lookup ON subscription_rules (city_id);
-- Needed to find the closest weather forecast for the notification time window
CREATE INDEX idx_weather_time_lookup ON weather_forecast (city_id, forecast_time);

-- Table comments for documentation
COMMENT ON TABLE subscription_rules IS 'Local replica of user rules from Subscription Service';
COMMENT ON TABLE weather_forecast IS 'Local replica of hourly weather data from Weather Service';
COMMENT ON TABLE alert_history IS 'Prevents duplicate notifications for the same rule and forecast point';
