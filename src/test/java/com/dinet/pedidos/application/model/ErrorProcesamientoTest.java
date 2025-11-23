package com.dinet.pedidos.application.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ErrorProcesamientoTest {

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        ErrorProcesamiento error = new ErrorProcesamiento();

        assertEquals(0, error.getNumeroLinea());
        assertNull(error.getMotivo());
        assertNull(error.getErrorCode());
    }

    @Test
    @DisplayName("Debería crear instancia con todos los argumentos")
    void shouldCreateInstanceWithAllArgsConstructor() {
        ErrorProcesamiento error = new ErrorProcesamiento(42, "Formato de fecha inválido", "DATE_FORMAT_ERROR");

        assertEquals(42, error.getNumeroLinea());
        assertEquals("Formato de fecha inválido", error.getMotivo());
        assertEquals("DATE_FORMAT_ERROR", error.getErrorCode());
    }

    @Test
    @DisplayName("Debería permitir modificar valores con setters")
    void shouldAllowModifyingValuesWithSetters() {
        ErrorProcesamiento error = new ErrorProcesamiento();

        error.setNumeroLinea(15);
        error.setMotivo("Campo requerido faltante");
        error.setErrorCode("REQUIRED_FIELD_MISSING");

        assertEquals(15, error.getNumeroLinea());
        assertEquals("Campo requerido faltante", error.getMotivo());
        assertEquals("REQUIRED_FIELD_MISSING", error.getErrorCode());
    }

    @Test
    @DisplayName("Debería comparar igualdad correctamente")
    void shouldCompareEqualityCorrectly() {
        ErrorProcesamiento error1 = new ErrorProcesamiento(1, "Error", "CODE");
        ErrorProcesamiento error2 = new ErrorProcesamiento(1, "Error", "CODE");

        assertEquals(error1.getNumeroLinea(), error2.getNumeroLinea());
        assertEquals(error1.getMotivo(), error2.getMotivo());
        assertEquals(error1.getErrorCode(), error2.getErrorCode());
    }
}