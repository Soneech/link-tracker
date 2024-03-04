--liquibase formatted sql

CREATE TABLE IF NOT EXISTS Chat_Link
(
    chat_id BIGINT NOT NULL REFERENCES Chat (id) ON DELETE CASCADE,
    link_id BIGINT NOT NULL REFERENCES Link (id) ON DELETE CASCADE
);
