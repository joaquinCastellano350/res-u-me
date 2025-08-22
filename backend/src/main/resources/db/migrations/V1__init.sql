CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users (
    id      UUID PRIMARY KEY,
    email   CITEXT UNIQUE NOT NULL,
    password_hash   TEXT NOT NULL,
    full_name   TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users(email)