package com.dinet.pedidos.infrastructure.repositories;

import com.dinet.pedidos.infrastructure.entities.CargaIdempotenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CargaIdempotenteJpaRepository extends JpaRepository<CargaIdempotenteEntity, UUID> {
    Optional<CargaIdempotenteEntity> findByIdempotencyKeyAndArchivoHash(String idempotencyKey, String archivoHash);
}