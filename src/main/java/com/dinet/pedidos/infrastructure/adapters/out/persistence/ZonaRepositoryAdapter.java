package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.Zona;
import com.dinet.pedidos.domain.ports.ZonaRepositoryPort;
import com.dinet.pedidos.infrastructure.entities.ZonaEntity;
import com.dinet.pedidos.infrastructure.repositories.ZonaJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ZonaRepositoryAdapter implements ZonaRepositoryPort {

    private final ZonaJpaRepository zonaJpaRepository;

    public ZonaRepositoryAdapter(ZonaJpaRepository zonaJpaRepository) {
        this.zonaJpaRepository = zonaJpaRepository;
    }

    @Override
    public Optional<Zona> findById(String id) {
        return zonaJpaRepository.findById(id)
                .map(this::toDomain);
    }

    public Zona toDomain(ZonaEntity entity) {
        return new Zona(entity.getId(), entity.isSoporteRefrigeracion());
    }
}