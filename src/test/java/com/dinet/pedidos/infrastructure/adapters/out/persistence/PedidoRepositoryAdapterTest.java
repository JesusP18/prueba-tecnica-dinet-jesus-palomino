package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.EstadoPedido;
import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.infrastructure.entities.PedidoEntity;
import com.dinet.pedidos.infrastructure.repositories.PedidoJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryAdapterTest {

    @Mock
    private PedidoJpaRepository pedidoJpaRepository;

    @InjectMocks
    private PedidoRepositoryAdapter pedidoRepositoryAdapter;

    @Test
    void save_ShouldSaveAndReturnPedido() {
        // Arrange
        Pedido pedido = Pedido.of(
                "PED001",
                "CLI001",
                "ZONA_NORTE",
                LocalDate.of(2024, 12, 31),
                EstadoPedido.PENDIENTE,
                true
        );

        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setNumeroPedido("PED001");
        entity.setClienteId("CLI001");
        entity.setZonaId("ZONA_NORTE");
        entity.setFechaEntrega(LocalDate.of(2024, 12, 31));
        entity.setEstado(EstadoPedido.PENDIENTE.name());
        entity.setRequiereRefrigeracion(true);

        when(pedidoJpaRepository.save(any(PedidoEntity.class))).thenReturn(entity);

        // Act
        Pedido savedPedido = pedidoRepositoryAdapter.save(pedido);

        // Assert
        assertThat(savedPedido).isNotNull();
        assertThat(savedPedido.getId()).isEqualTo(pedido.getId());
        assertThat(savedPedido.getNumeroPedido()).isEqualTo("PED001");
        assertThat(savedPedido.getClienteId()).isEqualTo("CLI001");
        assertThat(savedPedido.getZonaId()).isEqualTo("ZONA_NORTE");
        assertThat(savedPedido.getFechaEntrega()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(savedPedido.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(savedPedido.isRequiereRefrigeracion()).isTrue();
    }

    @Test
    void saveAll_ShouldSaveAllAndReturnPedidos() {
        // Arrange
        Pedido pedido1 = Pedido.of(
                "PED001",
                "CLI001",
                "ZONA_NORTE",
                LocalDate.of(2024, 12, 31),
                EstadoPedido.PENDIENTE,
                true
        );
        Pedido pedido2 = Pedido.of(
                "PED002",
                "CLI002",
                "ZONA_SUR",
                LocalDate.of(2024, 11, 30),
                EstadoPedido.CONFIRMADO,
                false
        );

        PedidoEntity entity1 = new PedidoEntity();
        entity1.setId(pedido1.getId());
        entity1.setNumeroPedido("PED001");
        entity1.setClienteId("CLI001");
        entity1.setZonaId("ZONA_NORTE");
        entity1.setFechaEntrega(LocalDate.of(2024, 12, 31));
        entity1.setEstado(EstadoPedido.PENDIENTE.name());
        entity1.setRequiereRefrigeracion(true);

        PedidoEntity entity2 = new PedidoEntity();
        entity2.setId(pedido2.getId());
        entity2.setNumeroPedido("PED002");
        entity2.setClienteId("CLI002");
        entity2.setZonaId("ZONA_SUR");
        entity2.setFechaEntrega(LocalDate.of(2024, 11, 30));
        entity2.setEstado(EstadoPedido.CONFIRMADO.name());
        entity2.setRequiereRefrigeracion(false);

        List<PedidoEntity> entities = List.of(entity1, entity2);

        when(pedidoJpaRepository.saveAll(anyList())).thenReturn(entities);

        // Act
        List<Pedido> savedPedidos = pedidoRepositoryAdapter.saveAll(List.of(pedido1, pedido2));

        // Assert
        assertThat(savedPedidos).hasSize(2);
        assertThat(savedPedidos).extracting(Pedido::getNumeroPedido)
                .containsExactly("PED001", "PED002");
    }

    @Test
    void existsByNumeroPedido_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(pedidoJpaRepository.existsByNumeroPedido("PED001")).thenReturn(true);

        // Act
        boolean exists = pedidoRepositoryAdapter.existsByNumeroPedido("PED001");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNumeroPedido_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(pedidoJpaRepository.existsByNumeroPedido("NON_EXISTENT")).thenReturn(false);

        // Act
        boolean exists = pedidoRepositoryAdapter.existsByNumeroPedido("NON_EXISTENT");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void toEntity_ShouldConvertPedidoToEntityCorrectly() {
        // Arrange
        Pedido pedido = Pedido.of(
                "PED003",
                "CLI003",
                "ZONA_TEST",
                LocalDate.of(2024, 10, 15),
                EstadoPedido.ENTREGADO,
                false
        );

        // Act
        PedidoEntity entity = pedidoRepositoryAdapter.toEntity(pedido);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(pedido.getId());
        assertThat(entity.getNumeroPedido()).isEqualTo("PED003");
        assertThat(entity.getClienteId()).isEqualTo("CLI003");
        assertThat(entity.getZonaId()).isEqualTo("ZONA_TEST");
        assertThat(entity.getFechaEntrega()).isEqualTo(LocalDate.of(2024, 10, 15));
        assertThat(entity.getEstado()).isEqualTo("ENTREGADO");
        assertThat(entity.isRequiereRefrigeracion()).isFalse();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void toDomain_ShouldConvertEntityToPedidoCorrectly() {
        // Arrange
        PedidoEntity entity = new PedidoEntity();
        entity.setId(UUID.randomUUID());
        entity.setNumeroPedido("PED004");
        entity.setClienteId("CLI004");
        entity.setZonaId("ZONA_TEST");
        entity.setFechaEntrega(LocalDate.of(2024, 9, 20));
        entity.setEstado("CONFIRMADO");
        entity.setRequiereRefrigeracion(true);

        // Act
        Pedido domain = pedidoRepositoryAdapter.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getNumeroPedido()).isEqualTo("PED004");
        assertThat(domain.getClienteId()).isEqualTo("CLI004");
        assertThat(domain.getZonaId()).isEqualTo("ZONA_TEST");
        assertThat(domain.getFechaEntrega()).isEqualTo(LocalDate.of(2024, 9, 20));
        assertThat(domain.getEstado()).isEqualTo(EstadoPedido.CONFIRMADO);
        assertThat(domain.isRequiereRefrigeracion()).isTrue();
    }
}