CREATE TABLE IF NOT EXISTS subscriptions
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    plan_name  VARCHAR(50)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
