--liquibase formatted sql
--fixes

ALTER TABLE Chat_Link ADD CONSTRAINT chat_id_link_id_pk_constraint PRIMARY KEY(chat_id, link_id);
