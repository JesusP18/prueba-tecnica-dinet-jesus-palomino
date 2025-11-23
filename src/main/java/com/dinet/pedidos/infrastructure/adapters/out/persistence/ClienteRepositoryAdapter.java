package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.Cliente;
import com.dinet.pedidos.domain.ports.ClienteRepositoryPort;
import com.dinet.pedidos.infrastructure.entities.ClienteEntity;
import com.dinet.pedidos.infrastructure.repositories.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    @Override
    public Optional<Cliente> findById(String id) {
        return clienteJpaRepository.findById(id)
                .map(this::toDomain);
    }

    public Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.isActivo());
    }
}