package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.CargaIdempotente;
import com.dinet.pedidos.infrastructure.entities.CargaIdempotenteEntity;
import com.dinet.pedidos.infrastructure.repositories.CargaIdempotenteJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CargaIdempotenteRepositoryAdapterTest {

    @Mock
    private CargaIdempotenteJpaRepository cargaIdempotenteJpaRepository;

    @InjectMocks
    private CargaIdempotenteRepositoryAdapter cargaIdempotenteRepositoryAdapter;

    @Test
    void findByIdempotencyKeyAndHash_WhenExists_ShouldReturnCargaIdempotente() {
        // Arrange
        String idempotencyKey = "key123";
        String hash = "hash123";
        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdempotencyKey(idempotencyKey);
        entity.setArchivoHash(hash);
        entity.setResultadoJson("{}");
        entity.setCreatedAt(LocalDateTime.now());

        when(cargaIdempotenteJpaRepository.findByIdempotencyKeyAndArchivoHash(idempotencyKey, hash))
                .thenReturn(Optional.of(entity));

        // Act
        Optional<CargaIdempotente> result = cargaIdempotenteRepositoryAdapter.findByIdempotencyKeyAndHash(idempotencyKey, hash);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getIdempotencyKey()).isEqualTo(idempotencyKey);
        assertThat(result.get().getArchivoHash()).isEqualTo(hash);
        assertThat(result.get().getResultadoJson()).isEqualTo("{}");
    }

    @Test
    void findByIdempotencyKeyAndHash_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(cargaIdempotenteJpaRepository.findByIdempotencyKeyAndArchivoHash("key", "hash"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CargaIdempotente> result = cargaIdempotenteRepositoryAdapter.findByIdempotencyKeyAndHash("key", "hash");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldSaveAndReturnCargaIdempotente() {
        // Arrange
        CargaIdempotente carga = new CargaIdempotente();
        carga.setIdempotencyKey("key123");
        carga.setArchivoHash("hash123");
        carga.setResultadoJson("{}");
        carga.setCreatedAt(LocalDateTime.now());

        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdempotencyKey("key123");
        entity.setArchivoHash("hash123");
        entity.setResultadoJson("{}");
        entity.setCreatedAt(carga.getCreatedAt());

        when(cargaIdempotenteJpaRepository.save(any(CargaIdempotenteEntity.class))).thenReturn(entity);

        // Act
        CargaIdempotente savedCarga = cargaIdempotenteRepositoryAdapter.save(carga);

        // Assert
        assertThat(savedCarga).isNotNull();
        assertThat(savedCarga.getId()).isNotNull();
        assertThat(savedCarga.getIdempotencyKey()).isEqualTo("key123");
        assertThat(savedCarga.getArchivoHash()).isEqualTo("hash123");
        assertThat(savedCarga.getResultadoJson()).isEqualTo("{}");
        assertThat(savedCarga.getCreatedAt()).isEqualTo(carga.getCreatedAt());
    }

    @Test
    void save_WithExistingId_ShouldUseExistingId() {
        // Arrange
        UUID existingId = UUID.randomUUID();
        CargaIdempotente carga = new CargaIdempotente();
        carga.setId(existingId);
        carga.setIdempotencyKey("key456");
        carga.setArchivoHash("hash456");
        carga.setResultadoJson("{\"test\": \"data\"}");
        carga.setCreatedAt(LocalDateTime.now());

        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(existingId);
        entity.setIdempotencyKey("key456");
        entity.setArchivoHash("hash456");
        entity.setResultadoJson("{\"test\": \"data\"}");
        entity.setCreatedAt(carga.getCreatedAt());

        when(cargaIdempotenteJpaRepository.save(any(CargaIdempotenteEntity.class))).thenReturn(entity);

        // Act
        CargaIdempotente savedCarga = cargaIdempotenteRepositoryAdapter.save(carga);

        // Assert
        assertThat(savedCarga.getId()).isEqualTo(existingId);
    }

    @Test
    void toEntity_ShouldConvertDomainToEntityCorrectly() {
        // Arrange
        CargaIdempotente domain = new CargaIdempotente();
        domain.setId(UUID.randomUUID());
        domain.setIdempotencyKey("test-key");
        domain.setArchivoHash("test-hash");
        domain.setResultadoJson("test-json");
        domain.setCreatedAt(LocalDateTime.now());

        // Act
        CargaIdempotenteEntity entity = cargaIdempotenteRepositoryAdapter.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getIdempotencyKey()).isEqualTo("test-key");
        assertThat(entity.getArchivoHash()).isEqualTo("test-hash");
        assertThat(entity.getResultadoJson()).isEqualTo("test-json");
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
    }

    @Test
    void toEntity_WithNullId_ShouldGenerateNewId() {
        // Arrange
        CargaIdempotente domain = new CargaIdempotente();
        domain.setId(null);
        domain.setIdempotencyKey("test-key");
        domain.setArchivoHash("test-hash");

        // Act
        CargaIdempotenteEntity entity = cargaIdempotenteRepositoryAdapter.toEntity(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    void toDomain_ShouldConvertEntityToDomainCorrectly() {
        // Arrange
        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(UUID.randomUUID());
        entity.setIdempotencyKey("entity-key");
        entity.setArchivoHash("entity-hash");
        entity.setResultadoJson("entity-json");
        entity.setCreatedAt(LocalDateTime.now());

        // Act
        CargaIdempotente domain = cargaIdempotenteRepositoryAdapter.toDomain(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getIdempotencyKey()).isEqualTo("entity-key");
        assertThat(domain.getArchivoHash()).isEqualTo("entity-hash");
        assertThat(domain.getResultadoJson()).isEqualTo("entity-json");
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}