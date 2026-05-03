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
    id                  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    city_id             INT                      NOT NULL REFERENCES monitored_cities (city_id) ON DELETE CASCADE,
    forecast_time       TIMESTAMP WITH TIME ZONE NOT NULL, -- "dt" field from JSON
    temp                DECIMAL(5, 2),
    feels_like          DECIMAL(5, 2),
    pressure            INT,
    humidity            INT,
    dew_point           DECIMAL(5, 2),
    uvi                 DECIMAL(5, 2),
    clouds              INT,
    visibility          INT,
    wind_speed          DECIMAL(5, 2),
    wind_deg            INT,
    wind_gust           DECIMAL(5, 2),
    pop                 DECIMAL(3, 2),                     -- Probability of precipitation (0.0 - 1.0)
    weather_main        VARCHAR(50),                       -- "Rain", "Clouds", etc.
    weather_description VARCHAR(100),
    weather_icon        VARCHAR(10),

    UNIQUE (city_id, forecast_time)
);

-- Index for the Condition Checker (Performance optimization)
-- CREATE INDEX idx_weather_lookup ON weather_hourly (city_id, forecast_time);

-- INSERT INTO monitored_cities (city_id, name, latitude, longitude, is_active, last_api_call, timezone)
-- VALUES (1, 'London', 51.5073, -0.1276, false, current_timestamp,  'Europe/London'),
--        (2, 'Bratislava',48.1435, 17.1083, false, current_timestamp,  'Europe/Bratislava'),
--        (3, 'Prague',50.0874, 14.4212, false, current_timestamp,  'Europe/Prague');
