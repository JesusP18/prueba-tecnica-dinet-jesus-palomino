package com.dinet.pedidos.domain.model;

import java.util.Objects;

/**
 * Cliente - modelo de dominio sencillo (solo lo que el dominio necesita).
 * En BD será una tabla con id (varchar) y activo (boolean).
 */
public class Cliente {

    private String id;
    private boolean activo;

    public Cliente(String id, boolean activo) {
        this.id = id;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public boolean isActivo() {
        return activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
