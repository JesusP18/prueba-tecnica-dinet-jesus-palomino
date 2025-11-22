package com.dinet.pedidos.infrastructure.repositories;

import com.dinet.pedidos.infrastructure.entities.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, UUID> {
    boolean existsByNumeroPedido(String numeroPedido);
}