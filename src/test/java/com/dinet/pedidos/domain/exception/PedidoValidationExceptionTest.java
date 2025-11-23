package com.dinet.pedidos.domain.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PedidoValidationExceptionTest {

    @Test
    @DisplayName("Debería crear excepción con código de error y mensaje")
    void shouldCreateExceptionWithErrorCodeAndMessage() {
        // Arrange
        String errorCode = "VALIDATION_ERROR";
        String message = "Error de validación en el pedido";

        // Act
        PedidoValidationException exception = new PedidoValidationException(errorCode, message);

        // Assert
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Debería crear excepción con código de error, mensaje y causa")
    void shouldCreateExceptionWithErrorCodeMessageAndCause() {
        // Arrange
        String errorCode = "INVALID_DATE";
        String message = "Fecha de entrega inválida";
        Throwable cause = new IllegalArgumentException("Formato de fecha incorrecto");

        // Act
        PedidoValidationException exception = new PedidoValidationException(errorCode, message, cause);

        // Assert
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("Formato de fecha incorrecto", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Debería ser instancia de RuntimeException")
    void shouldBeInstanceOfRuntimeException() {
        // Arrange & Act
        PedidoValidationException exception = new PedidoValidationException("TEST", "Test exception");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Debería mantener el código de error como final (inmutable)")
    void shouldMaintainErrorCodeAsFinal() {
        // Arrange
        String originalErrorCode = "ORIGINAL_CODE";
        PedidoValidationException exception = new PedidoValidationException(originalErrorCode, "Test");

        // Act & Assert
        // No hay setter para errorCode, lo que confirma que es inmutable una vez creado
        assertEquals(originalErrorCode, exception.getErrorCode());
    }

    @Test
    @DisplayName("Debería crear excepción con diferentes códigos de error")
    void shouldCreateExceptionWithDifferentErrorCodes() {
        // Arrange
        String[] errorCodes = {"REQUIRED_FIELD", "INVALID_FORMAT", "DUPLICATE_ORDER", "DATE_IN_PAST"};

        for (String errorCode : errorCodes) {
            // Act
            PedidoValidationException exception = new PedidoValidationException(errorCode, "Mensaje para " + errorCode);

            // Assert
            assertEquals(errorCode, exception.getErrorCode());
            assertEquals("Mensaje para " + errorCode, exception.getMessage());
        }
    }

    @Test
    @DisplayName("Debería propagar correctamente la causa subyacente")
    void shouldPropagateUnderlyingCauseCorrectly() {
        // Arrange
        String errorCode = "PARSING_ERROR";
        String message = "Error al parsear datos";
        NullPointerException rootCause = new NullPointerException("Valor nulo encontrado");

        // Act
        PedidoValidationException exception = new PedidoValidationException(errorCode, message, rootCause);

        // Assert
        assertEquals(rootCause, exception.getCause());
        assertTrue(exception.getCause() instanceof NullPointerException);
        assertEquals("Valor nulo encontrado", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Debería manejar códigos de error y mensajes nulos o vacíos")
    void shouldHandleNullOrEmptyErrorCodesAndMessages() {
        // Arrange & Act
        PedidoValidationException exception1 = new PedidoValidationException(null, null);
        PedidoValidationException exception2 = new PedidoValidationException("", "");
        PedidoValidationException exception3 = new PedidoValidationException(null, "Test message");
        PedidoValidationException exception4 = new PedidoValidationException("CODE", null);

        // Assert
        assertNull(exception1.getErrorCode());
        assertNull(exception1.getMessage());

        assertEquals("", exception2.getErrorCode());
        assertEquals("", exception2.getMessage());

        assertNull(exception3.getErrorCode());
        assertEquals("Test message", exception3.getMessage());

        assertEquals("CODE", exception4.getErrorCode());
        assertNull(exception4.getMessage());
    }
}