CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;
CREATE TABLE TODO(id BIGINT, title VARCHAR, done BOOLEAN, created_at TIMESTAMP, updated_at TIMESTAMP);