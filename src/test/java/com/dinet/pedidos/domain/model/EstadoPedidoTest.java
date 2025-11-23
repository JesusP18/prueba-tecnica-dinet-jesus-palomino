package com.dinet.pedidos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class EstadoPedidoTest {

    @Test
    @DisplayName("Debería tener los valores esperados")
    void shouldHaveExpectedValues() {
        // Act & Assert
        assertEquals(3, EstadoPedido.values().length);
        assertArrayEquals(new EstadoPedido[]{
                EstadoPedido.PENDIENTE,
                EstadoPedido.CONFIRMADO,
                EstadoPedido.ENTREGADO
        }, EstadoPedido.values());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "CONFIRMADO", "ENTREGADO"})
    @DisplayName("Debería parsear correctamente valores válidos")
    void shouldParseValidValuesCorrectly(String value) {
        // Act
        EstadoPedido estado = EstadoPedido.fromString(value);

        // Assert
        assertNotNull(estado);
        assertEquals(EstadoPedido.valueOf(value), estado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pendiente", "confirmado", "entregado", "PeNdIeNtE", "CONFIRMADO "})
    @DisplayName("Debería parsear correctamente valores con diferentes casos y espacios")
    void shouldParseValuesWithDifferentCasesAndSpaces(String value) {
        // Act
        EstadoPedido estado = EstadoPedido.fromString(value);

        // Assert
        assertNotNull(estado);
        assertEquals(EstadoPedido.valueOf(value.trim().toUpperCase()), estado);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Debería retornar null para valores nulos o vacíos")
    void shouldReturnNullForNullOrEmptyValues(String value) {
        // Act
        EstadoPedido estado = EstadoPedido.fromString(value);

        // Assert
        assertNull(estado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALIDO", "PENDIENT", "CONFIRMA", "ENTREG", "ESTADO_INEXISTENTE"})
    @DisplayName("Debería retornar null para valores inválidos")
    void shouldReturnNullForInvalidValues(String value) {
        // Act
        EstadoPedido estado = EstadoPedido.fromString(value);

        // Assert
        assertNull(estado);
    }

    @Test
    @DisplayName("Debería manejar valores con BOM o caracteres especiales")
    void shouldHandleValuesWithBomOrSpecialCharacters() {
        // Act & Assert
        assertNull(EstadoPedido.fromString("\uFEFFPENDIENTE")); // BOM
        assertNull(EstadoPedido.fromString("PENDIENTE\uFEFF")); // BOM al final
    }
}