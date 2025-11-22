package com.dinet.pedidos.infrastructure.repositories;

import com.dinet.pedidos.infrastructure.entities.ZonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaJpaRepository extends JpaRepository<ZonaEntity, String> {
}