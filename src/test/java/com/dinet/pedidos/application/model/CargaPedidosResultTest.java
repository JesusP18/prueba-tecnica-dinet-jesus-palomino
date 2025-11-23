package com.dinet.pedidos.application.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CargaPedidosResultTest {

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        CargaPedidosResult result = new CargaPedidosResult();

        assertEquals(0, result.getTotalProcesados());
        assertEquals(0, result.getGuardados());
        assertEquals(0, result.getConError());
        assertNotNull(result.getErrores());
        assertTrue(result.getErrores().isEmpty());
    }

    @Test
    @DisplayName("Debería crear instancia con todos los argumentos")
    void shouldCreateInstanceWithAllArgsConstructor() {
        List<ErrorProcesamiento> errores = List.of(
                new ErrorProcesamiento(1, "Error 1", "CODE_001"),
                new ErrorProcesamiento(2, "Error 2", "CODE_002")
        );

        CargaPedidosResult result = new CargaPedidosResult(100, 95, 5, errores);

        assertEquals(100, result.getTotalProcesados());
        assertEquals(95, result.getGuardados());
        assertEquals(5, result.getConError());
        assertEquals(2, result.getErrores().size());
    }

    @Test
    @DisplayName("Debería agregar error y actualizar contador")
    void shouldAddErrorAndUpdateCounter() {
        CargaPedidosResult result = new CargaPedidosResult();

        ErrorProcesamiento error1 = new ErrorProcesamiento(1, "Error de formato", "FORMAT_ERROR");
        ErrorProcesamiento error2 = new ErrorProcesamiento(2, "Error de validación", "VALIDATION_ERROR");

        result.agregarError(error1);
        assertEquals(1, result.getConError());
        assertEquals(1, result.getErrores().size());

        result.agregarError(error2);
        assertEquals(2, result.getConError());
        assertEquals(2, result.getErrores().size());
    }

    @Test
    @DisplayName("Debería mantener consistencia entre errores y contador")
    void shouldMaintainConsistencyBetweenErrorsAndCounter() {
        CargaPedidosResult result = new CargaPedidosResult();

        for (int i = 1; i <= 5; i++) {
            ErrorProcesamiento error = new ErrorProcesamiento(i, "Error " + i, "CODE_" + i);
            result.agregarError(error);
        }

        assertEquals(5, result.getConError());
        assertEquals(5, result.getErrores().size());
        assertEquals(result.getConError(), result.getErrores().size());
    }
}