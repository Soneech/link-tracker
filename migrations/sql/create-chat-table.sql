--liquibase formatted sql

CREATE TABLE IF NOT EXISTS Chat
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
