package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.ports.PedidoRepositoryPort;
import com.dinet.pedidos.infrastructure.entities.PedidoEntity;
import com.dinet.pedidos.infrastructure.repositories.PedidoJpaRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoJpaRepository pedidoJpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository pedidoJpaRepository) {
        this.pedidoJpaRepository = pedidoJpaRepository;
    }

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = toEntity(pedido);
        entity = pedidoJpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<Pedido> saveAll(List<Pedido> pedidos) {
        List<PedidoEntity> entities = pedidos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        entities = pedidoJpaRepository.saveAll(entities);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNumeroPedido(String numeroPedido) {
        return pedidoJpaRepository.existsByNumeroPedido(numeroPedido);
    }

    private PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setNumeroPedido(pedido.getNumeroPedido());
        entity.setClienteId(pedido.getClienteId());
        entity.setZonaId(pedido.getZonaId());
        entity.setFechaEntrega(pedido.getFechaEntrega());
        entity.setEstado(pedido.getEstado().name());
        entity.setRequiereRefrigeracion(pedido.isRequiereRefrigeracion());
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        return entity;
    }

    private Pedido toDomain(PedidoEntity entity) {
        return new Pedido(
                entity.getId(),
                entity.getNumeroPedido(),
                entity.getClienteId(),
                entity.getZonaId(),
                entity.getFechaEntrega(),
                com.dinet.pedidos.domain.model.EstadoPedido.valueOf(entity.getEstado()),
                entity.isRequiereRefrigeracion()
        );
    }
}