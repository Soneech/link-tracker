--liquibase formatted sql

ALTER TABLE Link
    ADD COLUMN last_update_time TIMESTAMP WITH TIME ZONE;
