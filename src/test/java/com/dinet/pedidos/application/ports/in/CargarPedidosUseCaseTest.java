package com.dinet.pedidos.application.ports.in;

import com.dinet.pedidos.application.model.CargaPedidosResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CargarPedidosUseCaseTest {

    @Test
    void testInterfaceDefinition() {
        // Esta es una prueba de contrato para verificar que la interfaz está bien definida
        CargarPedidosUseCase useCase = new CargarPedidosUseCase() {
            @Override
            public CargaPedidosResult cargarPedidos(MultipartFile file, String idempotencyKey) {
                return new CargaPedidosResult();
            }
        };

        assertNotNull(useCase);
    }
}