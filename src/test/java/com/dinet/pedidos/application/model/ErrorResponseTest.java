package com.dinet.pedidos.application.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertNull(errorResponse.getCode());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getCorrelationId());
        assertNull(errorResponse.getDetails());
    }

    @Test
    @DisplayName("Debería crear instancia con todos los argumentos")
    void shouldCreateInstanceWithAllArgsConstructor() {
        List<ErrorResponse.Detail> details = List.of(
                new ErrorResponse.Detail("campo1", "Error en campo1"),
                new ErrorResponse.Detail("campo2", "Error en campo2")
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_ERROR",
                "Error de validación en los datos",
                "corr-12345",
                details
        );

        assertEquals("VALIDATION_ERROR", errorResponse.getCode());
        assertEquals("Error de validación en los datos", errorResponse.getMessage());
        assertEquals("corr-12345", errorResponse.getCorrelationId());
        assertEquals(2, errorResponse.getDetails().size());
    }

    @Test
    @DisplayName("Debería configurar y obtener valores correctamente")
    void shouldSetAndGetValuesCorrectly() {
        ErrorResponse errorResponse = new ErrorResponse();

        ErrorResponse.Detail detail1 = new ErrorResponse.Detail();
        detail1.setField("email");
        detail1.setError("Formato de email inválido");

        ErrorResponse.Detail detail2 = new ErrorResponse.Detail();
        detail2.setField("password");
        detail2.setError("La contraseña es muy corta");

        errorResponse.setCode("AUTH_ERROR");
        errorResponse.setMessage("Error de autenticación");
        errorResponse.setCorrelationId("req-67890");
        errorResponse.setDetails(List.of(detail1, detail2));

        assertEquals("AUTH_ERROR", errorResponse.getCode());
        assertEquals("Error de autenticación", errorResponse.getMessage());
        assertEquals("req-67890", errorResponse.getCorrelationId());

        List<ErrorResponse.Detail> details = errorResponse.getDetails();
        assertEquals(2, details.size());
        assertEquals("email", details.get(0).getField());
        assertEquals("Formato de email inválido", details.get(0).getError());
        assertEquals("password", details.get(1).getField());
        assertEquals("La contraseña es muy corta", details.get(1).getError());
    }

    @Test
    @DisplayName("Debería funcionar correctamente la clase interna Detail")
    void shouldWorkCorrectlyWithInnerDetailClass() {
        ErrorResponse.Detail detail = new ErrorResponse.Detail();

        detail.setField("username");
        detail.setError("El usuario ya existe");

        assertEquals("username", detail.getField());
        assertEquals("El usuario ya existe", detail.getError());

        // Test constructor con argumentos
        ErrorResponse.Detail detail2 = new ErrorResponse.Detail("phone", "Número de teléfono inválido");
        assertEquals("phone", detail2.getField());
        assertEquals("Número de teléfono inválido", detail2.getError());
    }

    @Test
    @DisplayName("Debería manejar detalles nulos o vacíos")
    void shouldHandleNullOrEmptyDetails() {
        ErrorResponse errorResponse = new ErrorResponse("ERROR", "Mensaje", "corr-111", null);

        assertNull(errorResponse.getDetails());

        errorResponse.setDetails(List.of());
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }
}