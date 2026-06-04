-- Increase length of operator column to support longer condition names
ALTER TABLE subscription_rules ALTER COLUMN operator TYPE VARCHAR(20);
