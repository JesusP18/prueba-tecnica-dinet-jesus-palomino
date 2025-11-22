package com.dinet.pedidos.domain.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Zona - modelo de dominio para las zonas de entrega.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Zona {

    private String id;
    private boolean soporteRefrigeracion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zona)) return false;
        Zona zona = (Zona) o;
        return Objects.equals(id, zona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
