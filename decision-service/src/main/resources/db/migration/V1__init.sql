-- Decision Service Database Schema

-- Replica for Subscription Service data (for local decision)
CREATE TABLE subscriptions
(
    id                  UUID PRIMARY KEY,
    user_id             INT NOT NULL,
    city_id             INT NOT NULL,
    notify_before_hours INT                      DEFAULT 0,
    last_synced_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscription_rules
(
    id              UUID PRIMARY KEY,        -- Original ID from Subscription Service
    subscription_id UUID           NOT NULL,
    parameter_type  VARCHAR(50)    NOT NULL, -- e.g., TEMPERATURE, RAIN, WIND_SPEED
    operator        VARCHAR(20)    NOT NULL, -- e.g., GREATER_THAN, LESS_THAN, BETWEEN
    value_1         DECIMAL(10, 2) NOT NULL,
    value_2         DECIMAL(10, 2),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Replica of weather forecast data from Weather Service
CREATE TABLE weather_forecast
(
    id            SERIAL PRIMARY KEY,
    city_id       INT                      NOT NULL,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- Specific forecast timestamp (Unix dt)
    temp          DECIMAL(5, 2),
    humidity      INT,
    wind_speed    DECIMAL(5, 2),
    pop           DECIMAL(3, 2),                     -- Probability of Precipitation (0.0 - 1.0)
    weather_main  VARCHAR(50),                       -- e.g., Rain, Snow, Clouds
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (city_id, forecast_time)                  -- Ensures only one snapshot per city per hour
);

-- Alert History
-- Prevents spamming: "One notification per rule per specific forecast hour"
CREATE TABLE alert_history
(
    id            SERIAL PRIMARY KEY,
    rule_id       INT                      NOT NULL REFERENCES subscription_rules (id) ON DELETE CASCADE,
    forecast_time TIMESTAMP WITH TIME ZONE NOT NULL, -- The forecast timestamp that triggered the alert
    triggered_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (rule_id, forecast_time)                  -- Unique key to block duplicate alerts
);

-- Table comments for documentation
COMMENT ON TABLE subscriptions IS 'Local replica of user subscriptions from Subscription Service, used for decision logic';
COMMENT ON TABLE subscription_rules IS 'Local replica of subscription rules from Subscription Service, used for decision logic';
COMMENT ON TABLE weather_forecast IS 'Local replica of hourly weather data from Weather Service';
COMMENT ON TABLE alert_history IS 'Prevents duplicate notifications for the same rule and forecast point';
