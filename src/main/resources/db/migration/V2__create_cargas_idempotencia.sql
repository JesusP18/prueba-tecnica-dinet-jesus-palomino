-- V2__create_cargas_idempotencia.sql
CREATE TABLE IF NOT EXISTS cargas_idempotencia (
                                                   id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key varchar NOT NULL,
    archivo_hash varchar NOT NULL,
    resultado_json text NULL,
    created_at timestamp with time zone DEFAULT now(),
    UNIQUE (idempotency_key, archivo_hash)
    );

CREATE INDEX IF NOT EXISTS idx_cargas_idempotencia_key ON cargas_idempotencia (idempotency_key);
