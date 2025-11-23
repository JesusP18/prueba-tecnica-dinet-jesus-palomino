package com.dinet.pedidos.infrastructure.adapters.in.rest;

import com.dinet.pedidos.application.model.CargaPedidosResult;
import com.dinet.pedidos.application.ports.in.CargarPedidosUseCase;
import com.dinet.pedidos.infrastructure.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@ActiveProfiles("test")  // Activar perfil test
@Import(TestSecurityConfig.class)  // Importar configuración de seguridad para tests
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private static CargarPedidosUseCase cargarPedidosUseCase;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public CargarPedidosUseCase cargarPedidosUseCase() {
            return cargarPedidosUseCase;
        }
    }

    // Tus tests aquí (sin cambios)
    @Test
    void cargarPedidos_Success() throws Exception {
        // Given
        String idempotencyKey = "test-key-123";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "pedidos.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "cliente_id,zona_id,fecha_entrega,requiere_refrigeracion\nCLI001,ZON001,2024-01-15,true".getBytes()
        );

        CargaPedidosResult expectedResult = new CargaPedidosResult();
        expectedResult.setTotalProcesados(10);
        expectedResult.setGuardados(8);
        expectedResult.setConError(2);

        when(cargarPedidosUseCase.cargarPedidos(any(MockMultipartFile.class), eq(idempotencyKey)))
                .thenReturn(expectedResult);

        // When & Then
        mockMvc.perform(multipart("/pedidos/cargar")
                        .file(file)
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcesados").value(10))
                .andExpect(jsonPath("$.guardados").value(8))
                .andExpect(jsonPath("$.conError").value(2))
                .andExpect(jsonPath("$.errores").isArray());
    }

    // ... resto de los tests
}