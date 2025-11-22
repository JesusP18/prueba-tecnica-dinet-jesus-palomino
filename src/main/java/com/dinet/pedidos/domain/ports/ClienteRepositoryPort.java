package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Cliente;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Optional<Cliente> findById(String id);
}