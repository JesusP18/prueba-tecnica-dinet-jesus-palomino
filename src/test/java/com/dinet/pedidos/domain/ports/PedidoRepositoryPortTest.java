package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryPortTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Test
    void save_WithValidPedido_ShouldReturnSavedPedido() {
        // Arrange
        Pedido pedidoToSave = Pedido.of("PED001", "CLI001", "ZONA_NORTE",
                LocalDate.of(2024, 12, 31), EstadoPedido.PENDIENTE, true);

        Pedido savedPedido = Pedido.of("PED001", "CLI001", "ZONA_NORTE",
                LocalDate.of(2024, 12, 31), EstadoPedido.PENDIENTE, true);

        when(pedidoRepositoryPort.save(any(Pedido.class)))
                .thenReturn(savedPedido);

        // Act
        Pedido result = pedidoRepositoryPort.save(pedidoToSave);

        // Assert
        assertNotNull(result);
        assertEquals("PED001", result.getNumeroPedido());
        assertEquals("CLI001", result.getClienteId());
        assertEquals("ZONA_NORTE", result.getZonaId());
        assertEquals(EstadoPedido.PENDIENTE, result.getEstado());
        assertTrue(result.isRequiereRefrigeracion());
        verify(pedidoRepositoryPort).save(pedidoToSave);
    }

    @Test
    void saveAll_WithListOfPedidos_ShouldReturnSavedPedidos() {
        // Arrange
        Pedido pedido1 = Pedido.of("PED001", "CLI001", "ZONA_NORTE",
                LocalDate.of(2024, 12, 31), EstadoPedido.PENDIENTE, true);
        Pedido pedido2 = Pedido.of("PED002", "CLI002", "ZONA_SUR",
                LocalDate.of(2024, 12, 25), EstadoPedido.CONFIRMADO, false);

        List<Pedido> pedidosToSave = Arrays.asList(pedido1, pedido2);
        List<Pedido> savedPedidos = Arrays.asList(pedido1, pedido2);

        when(pedidoRepositoryPort.saveAll(anyList()))
                .thenReturn(savedPedidos);

        // Act
        List<Pedido> result = pedidoRepositoryPort.saveAll(pedidosToSave);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PED001", result.get(0).getNumeroPedido());
        assertEquals("PED002", result.get(1).getNumeroPedido());
        verify(pedidoRepositoryPort).saveAll(pedidosToSave);
    }

    @Test
    void saveAll_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        List<Pedido> emptyList = Arrays.asList();

        when(pedidoRepositoryPort.saveAll(emptyList))
                .thenReturn(emptyList);

        // Act
        List<Pedido> result = pedidoRepositoryPort.saveAll(emptyList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pedidoRepositoryPort).saveAll(emptyList);
    }

    @Test
    void existsByNumeroPedido_WithExistingNumeroPedido_ShouldReturnTrue() {
        // Arrange
        String existingNumeroPedido = "PED001";

        when(pedidoRepositoryPort.existsByNumeroPedido(existingNumeroPedido))
                .thenReturn(true);

        // Act
        boolean result = pedidoRepositoryPort.existsByNumeroPedido(existingNumeroPedido);

        // Assert
        assertTrue(result);
        verify(pedidoRepositoryPort).existsByNumeroPedido(existingNumeroPedido);
    }

    @Test
    void existsByNumeroPedido_WithNonExistingNumeroPedido_ShouldReturnFalse() {
        // Arrange
        String nonExistingNumeroPedido = "PED999";

        when(pedidoRepositoryPort.existsByNumeroPedido(nonExistingNumeroPedido))
                .thenReturn(false);

        // Act
        boolean result = pedidoRepositoryPort.existsByNumeroPedido(nonExistingNumeroPedido);

        // Assert
        assertFalse(result);
        verify(pedidoRepositoryPort).existsByNumeroPedido(nonExistingNumeroPedido);
    }

    @Test
    void existsByNumeroPedido_WithNullNumeroPedido_ShouldReturnFalse() {
        // Arrange
        when(pedidoRepositoryPort.existsByNumeroPedido(null))
                .thenReturn(false);

        // Act
        boolean result = pedidoRepositoryPort.existsByNumeroPedido(null);

        // Assert
        assertFalse(result);
        verify(pedidoRepositoryPort).existsByNumeroPedido(null);
    }
}