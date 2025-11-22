package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Zona;
import java.util.Optional;

public interface ZonaRepositoryPort {
    Optional<Zona> findById(String id);
}