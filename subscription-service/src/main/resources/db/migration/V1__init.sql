CREATE TABLE IF NOT EXISTS subscriptions
(
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT       NOT NULL,
    city                  VARCHAR(100) NOT NULL,
    temperature_threshold DOUBLE PRECISION NOT NULL,
    condition             VARCHAR(10)  NOT NULL, -- ABOVE, BELOW
    last_notified_at      TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
