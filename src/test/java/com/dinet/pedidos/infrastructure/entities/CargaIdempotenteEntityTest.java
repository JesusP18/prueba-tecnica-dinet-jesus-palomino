// CargaIdempotenteEntityTest.java
package com.dinet.pedidos.infrastructure.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class CargaIdempotenteEntityTest {

    @Test
    void shouldCreateCargaIdempotenteEntity() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(id);
        entity.setIdempotencyKey("TEST_KEY");
        entity.setArchivoHash("ABC123");
        entity.setResultadoJson("{\"status\":\"success\"}");
        entity.setCreatedAt(now);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getIdempotencyKey()).isEqualTo("TEST_KEY");
        assertThat(entity.getArchivoHash()).isEqualTo("ABC123");
        assertThat(entity.getResultadoJson()).isEqualTo("{\"status\":\"success\"}");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void shouldCreateCargaIdempotenteEntityWithAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        CargaIdempotenteEntity entity = new CargaIdempotenteEntity(
                id, "TEST_KEY", "ABC123", "{\"status\":\"success\"}", now
        );

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getIdempotencyKey()).isEqualTo("TEST_KEY");
        assertThat(entity.getArchivoHash()).isEqualTo("ABC123");
        assertThat(entity.getResultadoJson()).isEqualTo("{\"status\":\"success\"}");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
    }
}