CREATE TABLE IF NOT EXISTS USER(id BIGINT PRIMARY KEY, username VARCHAR NOT NULL UNIQUE, password VARCHAR NOT NULL);