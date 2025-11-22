-- V1__init_schema.sql
-- Inicial: clientes, zonas, pedidos (nombres en snake_case)

-- extensiones (opcional: para funciones UUID)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tabla clientes
CREATE TABLE IF NOT EXISTS clientes (
                                        id varchar PRIMARY KEY,
                                        activo boolean NOT NULL DEFAULT true
);

-- Tabla zonas
CREATE TABLE IF NOT EXISTS zonas (
                                     id varchar PRIMARY KEY,
                                     soporte_refrigeracion boolean NOT NULL DEFAULT false
);

-- Tabla pedidos
CREATE TABLE IF NOT EXISTS pedidos (
                                       id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_pedido varchar NOT NULL,
    cliente_id varchar NOT NULL,
    zona_id varchar NOT NULL,
    fecha_entrega date NOT NULL,
    estado varchar NOT NULL CHECK (estado IN ('PENDIENTE','CONFIRMADO','ENTREGADO')),
    requiere_refrigeracion boolean NOT NULL,
    created_at timestamp with time zone DEFAULT now(),
    updated_at timestamp with time zone DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uk_pedidos_numero_pedido ON pedidos (numero_pedido);
CREATE INDEX IF NOT EXISTS idx_pedidos_estado_fecha ON pedidos (estado, fecha_entrega);
