--liquibase formatted sql

BEGIN;
ALTER TABLE Chat
    ADD COLUMN registered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

UPDATE Chat
    SET registered_at = CURRENT_TIMESTAMP;

ALTER TABLE Chat
    ALTER COLUMN registered_at SET NOT NULL;
COMMIT;
