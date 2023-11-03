-- liquibase formatted sql

--changeset kupriyanova:1
CREATE table notification_task (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT,
    notification TEXT,
    date TIMESTAMP
)