-- Subscription Service Database Schema
-- Focus: Reference Data (Cities) and User Intent (Subscriptions)

-- Cities lookup table (Reference data)
CREATE TABLE cities
(
    id                         SERIAL PRIMARY KEY,
    name                       VARCHAR(100)  NOT NULL UNIQUE,
    latitude                   DECIMAL(9, 6) NOT NULL,
    longitude                  DECIMAL(9, 6) NOT NULL,
    timezone                   VARCHAR(50),
    city_status                VARCHAR(16) DEFAULT 'NOT_USED',
    active_subscriptions_count INT         DEFAULT 0
);

-- Subscriptions (User intent)
CREATE TABLE subscriptions
(
    id                  UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id             INT NOT NULL, -- Reference to IAM User
    city_id             INT NOT NULL REFERENCES cities (id),
    notify_before_hours INT                      DEFAULT 0,
    is_active           BOOLEAN                  DEFAULT true,
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Subscription Rules (Flexible conditions)
CREATE TABLE subscription_rules
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscription_id UUID           NOT NULL REFERENCES subscriptions (id) ON DELETE CASCADE,
    parameter_type  VARCHAR(20)    NOT NULL, -- e.g., 'TEMPERATURE', 'HUMIDITY', 'RAIN'
    operator        VARCHAR(10)    NOT NULL, -- e.g., 'GT', 'LT', 'BETWEEN'
    value_1         DECIMAL(10, 2) NOT NULL,
    value_2         DECIMAL(10, 2)           -- Used for 'BETWEEN' operator
);

-- Initial Data
INSERT INTO cities (name, latitude, longitude, timezone)
VALUES ('London', 51.5073, -0.1276, 'Europe/London'),
       ('Bratislava', 48.1435, 17.1083, 'Europe/Bratislava'),
       ('Prague', 50.0874, 14.4212, 'Europe/Prague');
