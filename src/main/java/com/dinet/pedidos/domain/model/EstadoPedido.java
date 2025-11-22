package com.dinet.pedidos.domain.model;

/**
 * Enum del estado del pedido — coincide con el CHECK en BD.
 */
public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    ENTREGADO;

    public static EstadoPedido fromString(String value) {
        if (value == null) return null;
        try {
            return EstadoPedido.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
