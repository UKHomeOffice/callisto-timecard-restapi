CREATE SCHEMA IF NOT EXISTS timecard;

CREATE TABLE IF NOT EXISTS moo (
    id 	SERIAL PRIMARY KEY,
    description VARCHAR(250) NOT NULL
);