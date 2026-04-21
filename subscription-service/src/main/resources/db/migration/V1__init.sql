-- Subscription Service Database Schema
-- Focus: Reference Data (Cities) and User Intent (Subscriptions)

-- 1. Custom type for city monitoring status
DO
$$
    BEGIN
        CREATE TYPE city_status AS ENUM ('ON_USE', 'NOT_USED');
    EXCEPTION
        WHEN duplicate_object THEN null;
    END
$$;

-- 2. Cities lookup table (Reference data)
CREATE TABLE cities
(
    id                         SERIAL PRIMARY KEY,
    name                       VARCHAR(100)  NOT NULL UNIQUE,
    latitude                   DECIMAL(9, 6) NOT NULL,
    longitude                  DECIMAL(9, 6) NOT NULL,
    timezone                   VARCHAR(50),
    status                     city_status DEFAULT 'NOT_USED',
    active_subscriptions_count INT         DEFAULT 0
);

-- 3. Subscriptions (User intent)
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

-- 4. Subscription Rules (Flexible conditions)
CREATE TABLE subscription_rules
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscription_id UUID           NOT NULL REFERENCES subscriptions (id) ON DELETE CASCADE,
    parameter_type  VARCHAR(20)    NOT NULL, -- e.g., 'TEMPERATURE', 'HUMIDITY', 'RAIN'
    operator        VARCHAR(10)    NOT NULL, -- e.g., 'GT', 'LT', 'BETWEEN'
    value_1         DECIMAL(10, 2) NOT NULL,
    value_2         DECIMAL(10, 2)           -- Used for 'BETWEEN' operator
);

-- 5. Trigger Function to auto-update city status based on active subscriptions
CREATE OR REPLACE FUNCTION refresh_city_status()
    RETURNS TRIGGER AS
$$
DECLARE
    target_city_id INT;
BEGIN
    -- Determine which city needs status update
    IF (TG_OP = 'DELETE') THEN
        target_city_id := OLD.city_id;
    ELSE
        target_city_id := NEW.city_id;
    END IF;

    -- Update count for the city
    UPDATE cities
    SET active_subscriptions_count = (SELECT count(*)
                                      FROM subscriptions
                                      WHERE city_id = target_city_id
                                        AND is_active = true)
    WHERE id = target_city_id;

    -- Update status based on the new count
    UPDATE cities
    SET status = CASE
                     WHEN active_subscriptions_count > 0 THEN 'ON_USE'::city_status
                     ELSE 'NOT_USED'::city_status
        END
    WHERE id = target_city_id;

    -- If it's an update and city_id changed, we also need to update the OLD city
    IF (TG_OP = 'UPDATE' AND OLD.city_id <> NEW.city_id) THEN
        UPDATE cities
        SET active_subscriptions_count = (SELECT count(*)
                                          FROM subscriptions
                                          WHERE city_id = OLD.city_id
                                            AND is_active = true)
        WHERE id = OLD.city_id;

        UPDATE cities
        SET status = CASE
                         WHEN active_subscriptions_count > 0 THEN 'ON_USE'::city_status
                         ELSE 'NOT_USED'::city_status
            END
        WHERE id = OLD.city_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql
    SET search_path = v1_subscription_service, public;

-- 6. Attach Trigger to subscriptions table
CREATE TRIGGER trg_subscriptions_change
    AFTER INSERT OR UPDATE OR DELETE
    ON subscriptions
    FOR EACH ROW
EXECUTE FUNCTION refresh_city_status();

-- 7. Initial Data
INSERT INTO cities (name, latitude, longitude, timezone)
VALUES ('London', 51.5073, -0.1276, 'Europe/London'),
       ('Bratislava', 48.1435, 17.1083, 'Europe/Bratislava'),
       ('Prague', 50.0874, 14.4212, 'Europe/Prague');
