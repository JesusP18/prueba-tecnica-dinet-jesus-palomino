package com.dinet.pedidos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CargaIdempotenteTest {

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        CargaIdempotente carga = new CargaIdempotente();

        assertNull(carga.getId());
        assertNull(carga.getIdempotencyKey());
        assertNull(carga.getArchivoHash());
        assertNull(carga.getResultadoJson());
        assertNull(carga.getCreatedAt());
    }

    @Test
    @DisplayName("Debería crear instancia con todos los argumentos")
    void shouldCreateInstanceWithAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        String idempotencyKey = "test-key-123";
        String archivoHash = "abc123def456";
        String resultadoJson = "{\"total\": 100}";
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        CargaIdempotente carga = new CargaIdempotente(id, idempotencyKey, archivoHash, resultadoJson, createdAt);

        // Assert
        assertEquals(id, carga.getId());
        assertEquals(idempotencyKey, carga.getIdempotencyKey());
        assertEquals(archivoHash, carga.getArchivoHash());
        assertEquals(resultadoJson, carga.getResultadoJson());
        assertEquals(createdAt, carga.getCreatedAt());
    }

    @Test
    @DisplayName("Debería permitir modificar todos los valores con setters")
    void shouldAllowModifyingAllValuesWithSetters() {
        // Arrange
        CargaIdempotente carga = new CargaIdempotente();
        UUID id = UUID.randomUUID();
        String idempotencyKey = "test-key-456";
        String archivoHash = "def789ghi012";
        String resultadoJson = "{\"total\": 50}";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        // Act
        carga.setId(id);
        carga.setIdempotencyKey(idempotencyKey);
        carga.setArchivoHash(archivoHash);
        carga.setResultadoJson(resultadoJson);
        carga.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, carga.getId());
        assertEquals(idempotencyKey, carga.getIdempotencyKey());
        assertEquals(archivoHash, carga.getArchivoHash());
        assertEquals(resultadoJson, carga.getResultadoJson());
        assertEquals(createdAt, carga.getCreatedAt());
    }

    @Test
    @DisplayName("Debería manejar valores nulos correctamente")
    void shouldHandleNullValuesCorrectly() {
        // Arrange
        CargaIdempotente carga = new CargaIdempotente(null, null, null, null, null);

        // Assert
        assertNull(carga.getId());
        assertNull(carga.getIdempotencyKey());
        assertNull(carga.getArchivoHash());
        assertNull(carga.getResultadoJson());
        assertNull(carga.getCreatedAt());
    }

    @Test
    @DisplayName("Debería permitir cambiar valores después de la creación")
    void shouldAllowChangingValuesAfterCreation() {
        // Arrange
        CargaIdempotente carga = new CargaIdempotente();
        UUID initialId = UUID.randomUUID();
        carga.setId(initialId);
        carga.setIdempotencyKey("initial-key");
        carga.setArchivoHash("initial-hash");
        carga.setResultadoJson("initial-json");
        carga.setCreatedAt(LocalDateTime.now());

        // Act - Cambiar todos los valores
        UUID newId = UUID.randomUUID();
        carga.setId(newId);
        carga.setIdempotencyKey("new-key");
        carga.setArchivoHash("new-hash");
        carga.setResultadoJson("new-json");
        LocalDateTime newCreatedAt = LocalDateTime.now().plusHours(1);
        carga.setCreatedAt(newCreatedAt);

        // Assert
        assertEquals(newId, carga.getId());
        assertEquals("new-key", carga.getIdempotencyKey());
        assertEquals("new-hash", carga.getArchivoHash());
        assertEquals("new-json", carga.getResultadoJson());
        assertEquals(newCreatedAt, carga.getCreatedAt());
    }
}