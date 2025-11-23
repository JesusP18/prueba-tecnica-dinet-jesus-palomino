package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.CargaIdempotente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CargaIdempotenteRepositoryPortTest {

    @Mock
    private CargaIdempotenteRepositoryPort cargaIdempotenteRepositoryPort;

    @Test
    void findByIdempotencyKeyAndHash_WithExistingKeyAndHash_ShouldReturnCarga() {
        // Arrange
        String idempotencyKey = "test-key-123";
        String fileHash = "abc123def456";
        CargaIdempotente expectedCarga = new CargaIdempotente();
        expectedCarga.setId(UUID.randomUUID());
        expectedCarga.setIdempotencyKey(idempotencyKey);
        expectedCarga.setArchivoHash(fileHash);

        when(cargaIdempotenteRepositoryPort.findByIdempotencyKeyAndHash(idempotencyKey, fileHash))
                .thenReturn(Optional.of(expectedCarga));

        // Act
        Optional<CargaIdempotente> result = cargaIdempotenteRepositoryPort.findByIdempotencyKeyAndHash(idempotencyKey, fileHash);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedCarga, result.get());
        verify(cargaIdempotenteRepositoryPort).findByIdempotencyKeyAndHash(idempotencyKey, fileHash);
    }

    @Test
    void findByIdempotencyKeyAndHash_WithNonExistingKeyAndHash_ShouldReturnEmpty() {
        // Arrange
        String idempotencyKey = "non-existing-key";
        String fileHash = "non-existing-hash";

        when(cargaIdempotenteRepositoryPort.findByIdempotencyKeyAndHash(idempotencyKey, fileHash))
                .thenReturn(Optional.empty());

        // Act
        Optional<CargaIdempotente> result = cargaIdempotenteRepositoryPort.findByIdempotencyKeyAndHash(idempotencyKey, fileHash);

        // Assert
        assertFalse(result.isPresent());
        verify(cargaIdempotenteRepositoryPort).findByIdempotencyKeyAndHash(idempotencyKey, fileHash);
    }

    @Test
    void save_WithValidCarga_ShouldReturnSavedCarga() {
        // Arrange
        CargaIdempotente cargaToSave = new CargaIdempotente();
        cargaToSave.setIdempotencyKey("test-key");
        cargaToSave.setArchivoHash("test-hash");

        CargaIdempotente savedCarga = new CargaIdempotente();
        savedCarga.setId(UUID.randomUUID());
        savedCarga.setIdempotencyKey("test-key");
        savedCarga.setArchivoHash("test-hash");

        when(cargaIdempotenteRepositoryPort.save(any(CargaIdempotente.class)))
                .thenReturn(savedCarga);

        // Act
        CargaIdempotente result = cargaIdempotenteRepositoryPort.save(cargaToSave);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("test-key", result.getIdempotencyKey());
        assertEquals("test-hash", result.getArchivoHash());
        verify(cargaIdempotenteRepositoryPort).save(cargaToSave);
    }

    @Test
    void save_WithNullCarga_ShouldHandleGracefully() {
        // Arrange
        when(cargaIdempotenteRepositoryPort.save(null))
                .thenReturn(null);

        // Act
        CargaIdempotente result = cargaIdempotenteRepositoryPort.save(null);

        // Assert
        assertNull(result);
        verify(cargaIdempotenteRepositoryPort).save(null);
    }
}