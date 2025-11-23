package com.dinet.pedidos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    @DisplayName("Debería crear pedido con factory method")
    void shouldCreatePedidoWithFactoryMethod() {
        // Arrange
        String numeroPedido = "PED001";
        String clienteId = "CLI001";
        String zonaId = "ZONA_NORTE";
        LocalDate fechaEntrega = LocalDate.of(2024, 12, 31);
        EstadoPedido estado = EstadoPedido.PENDIENTE;
        boolean requiereRefrigeracion = true;

        // Act
        Pedido pedido = Pedido.of(numeroPedido, clienteId, zonaId, fechaEntrega, estado, requiereRefrigeracion);

        // Assert
        assertNotNull(pedido.getId());
        assertEquals(numeroPedido, pedido.getNumeroPedido());
        assertEquals(clienteId, pedido.getClienteId());
        assertEquals(zonaId, pedido.getZonaId());
        assertEquals(fechaEntrega, pedido.getFechaEntrega());
        assertEquals(estado, pedido.getEstado());
        assertEquals(requiereRefrigeracion, pedido.isRequiereRefrigeracion());
    }

    @Test
    @DisplayName("Debería crear pedido con constructor completo")
    void shouldCreatePedidoWithAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        String numeroPedido = "PED002";
        String clienteId = "CLI002";
        String zonaId = "ZONA_SUR";
        LocalDate fechaEntrega = LocalDate.of(2024, 11, 15);
        EstadoPedido estado = EstadoPedido.CONFIRMADO;
        boolean requiereRefrigeracion = false;

        // Act
        Pedido pedido = new Pedido(id, numeroPedido, clienteId, zonaId, fechaEntrega, estado, requiereRefrigeracion);

        // Assert
        assertEquals(id, pedido.getId());
        assertEquals(numeroPedido, pedido.getNumeroPedido());
        assertEquals(clienteId, pedido.getClienteId());
        assertEquals(zonaId, pedido.getZonaId());
        assertEquals(fechaEntrega, pedido.getFechaEntrega());
        assertEquals(estado, pedido.getEstado());
        assertEquals(requiereRefrigeracion, pedido.isRequiereRefrigeracion());
    }

    @Test
    @DisplayName("Debería considerar iguales dos pedidos con el mismo número de pedido")
    void shouldConsiderEqualPedidosWithSameNumeroPedido() {
        // Arrange
        Pedido pedido1 = Pedido.of("PED001", "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);
        Pedido pedido2 = Pedido.of("PED001", "CLI002", "ZONA_SUR", LocalDate.now().plusDays(1), EstadoPedido.ENTREGADO, false);

        // Act & Assert
        assertEquals(pedido1, pedido2);
        assertEquals(pedido1.hashCode(), pedido2.hashCode());
    }

    @Test
    @DisplayName("Debería considerar diferentes dos pedidos con diferente número de pedido")
    void shouldConsiderDifferentPedidosWithDifferentNumeroPedido() {
        // Arrange
        Pedido pedido1 = Pedido.of("PED001", "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);
        Pedido pedido2 = Pedido.of("PED002", "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);

        // Act & Assert
        assertNotEquals(pedido1, pedido2);
        assertNotEquals(pedido1.hashCode(), pedido2.hashCode());
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con null")
    void shouldReturnFalseWhenComparedWithNull() {
        // Arrange
        Pedido pedido = Pedido.of("PED001", "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);

        // Act & Assert
        assertNotEquals(null, pedido);
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con objeto de diferente tipo")
    void shouldReturnFalseWhenComparedWithDifferentType() {
        // Arrange
        Pedido pedido = Pedido.of("PED001", "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);

        // Act & Assert
        assertNotEquals("PED001", pedido);
    }

    @Test
    @DisplayName("Debería generar toString con información completa")
    void shouldGenerateToStringWithCompleteInformation() {
        // Arrange
        Pedido pedido = Pedido.of("PED001", "CLI001", "ZONA_NORTE", LocalDate.of(2024, 12, 31), EstadoPedido.PENDIENTE, true);

        // Act
        String toString = pedido.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("PED001"));
        assertTrue(toString.contains("CLI001"));
        assertTrue(toString.contains("ZONA_NORTE"));
        assertTrue(toString.contains("2024-12-31"));
        assertTrue(toString.contains("PENDIENTE"));
        assertTrue(toString.contains("true"));
    }

    @Test
    @DisplayName("Debería manejar pedido con número de pedido null en equals y hashCode")
    void shouldHandlePedidoWithNullNumeroPedidoInEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        Pedido pedido1 = new Pedido(id, null, "CLI001", "ZONA_NORTE", LocalDate.now(), EstadoPedido.PENDIENTE, true);
        Pedido pedido2 = new Pedido(id, null, "CLI002", "ZONA_SUR", LocalDate.now(), EstadoPedido.CONFIRMADO, false);

        // Act & Assert
        assertEquals(pedido1, pedido2); // Ambos tienen null en numeroPedido
        assertEquals(0, pedido1.hashCode());
        assertEquals(0, pedido2.hashCode());
    }
}