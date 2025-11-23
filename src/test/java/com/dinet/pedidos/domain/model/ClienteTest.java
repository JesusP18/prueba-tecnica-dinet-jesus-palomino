package com.dinet.pedidos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    @DisplayName("Debería crear cliente con constructor")
    void shouldCreateClienteWithConstructor() {
        // Arrange & Act
        Cliente cliente = new Cliente("CLI001", true);

        // Assert
        assertEquals("CLI001", cliente.getId());
        assertTrue(cliente.isActivo());
    }

    @Test
    @DisplayName("Debería crear cliente inactivo")
    void shouldCreateInactiveCliente() {
        // Arrange & Act
        Cliente cliente = new Cliente("CLI002", false);

        // Assert
        assertEquals("CLI002", cliente.getId());
        assertFalse(cliente.isActivo());
    }

    @Test
    @DisplayName("Debería considerar iguales dos clientes con el mismo ID")
    void shouldConsiderEqualClientesWithSameId() {
        // Arrange
        Cliente cliente1 = new Cliente("CLI001", true);
        Cliente cliente2 = new Cliente("CLI001", false); // Diferente estado activo

        // Act & Assert
        assertEquals(cliente1, cliente2);
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
    }

    @Test
    @DisplayName("Debería considerar diferentes dos clientes con diferente ID")
    void shouldConsiderDifferentClientesWithDifferentId() {
        // Arrange
        Cliente cliente1 = new Cliente("CLI001", true);
        Cliente cliente2 = new Cliente("CLI002", true);

        // Act & Assert
        assertNotEquals(cliente1, cliente2);
        assertNotEquals(cliente1.hashCode(), cliente2.hashCode());
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con null")
    void shouldReturnFalseWhenComparedWithNull() {
        // Arrange
        Cliente cliente = new Cliente("CLI001", true);

        // Act & Assert
        assertNotEquals(null, cliente);
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con objeto de diferente tipo")
    void shouldReturnFalseWhenComparedWithDifferentType() {
        // Arrange
        Cliente cliente = new Cliente("CLI001", true);

        // Act & Assert
        assertNotEquals("CLI001", cliente);
    }

    @Test
    @DisplayName("Debería ser igual a sí mismo")
    void shouldBeEqualToItself() {
        // Arrange
        Cliente cliente = new Cliente("CLI001", true);

        // Act & Assert
        assertEquals(cliente, cliente);
    }

    @Test
    @DisplayName("Debería manejar cliente con ID null en equals y hashCode")
    void shouldHandleClienteWithNullIdInEqualsAndHashCode() {
        // Arrange
        Cliente cliente1 = new Cliente(null, true);
        Cliente cliente2 = new Cliente(null, false);

        // Act & Assert
        assertEquals(cliente1, cliente2);
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
    }
}