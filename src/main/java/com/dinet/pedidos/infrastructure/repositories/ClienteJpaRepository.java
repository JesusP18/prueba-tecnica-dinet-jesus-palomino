package com.dinet.pedidos.infrastructure.repositories;

import com.dinet.pedidos.infrastructure.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String> {
}