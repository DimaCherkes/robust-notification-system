CREATE SCHEMA IF NOT EXISTS v1_notification_service;

CREATE TABLE IF NOT EXISTS v1_notification_service.users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS v1_notification_service.notification_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    subscription_id UUID NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL, -- e.g., SENT, FAILED, PENDING
    error_message TEXT,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    retry_count INT DEFAULT 0,
    
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES v1_notification_service.users(id)
);

CREATE INDEX idx_users_email ON v1_notification_service.users(email);
CREATE INDEX idx_notification_history_user_id ON v1_notification_service.notification_history(user_id);
CREATE INDEX idx_notification_history_subscription_id ON v1_notification_service.notification_history(subscription_id);
CREATE INDEX idx_notification_history_status ON v1_notification_service.notification_history(status);
