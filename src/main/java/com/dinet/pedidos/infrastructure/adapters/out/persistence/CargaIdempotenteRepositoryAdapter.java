package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.CargaIdempotente;
import com.dinet.pedidos.domain.ports.CargaIdempotenteRepositoryPort;
import com.dinet.pedidos.infrastructure.entities.CargaIdempotenteEntity;
import com.dinet.pedidos.infrastructure.repositories.CargaIdempotenteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CargaIdempotenteRepositoryAdapter implements CargaIdempotenteRepositoryPort {

    private final CargaIdempotenteJpaRepository cargaIdempotenteJpaRepository;

    @Override
    public Optional<CargaIdempotente> findByIdempotencyKeyAndHash(String idempotencyKey, String hash) {
        return cargaIdempotenteJpaRepository.findByIdempotencyKeyAndArchivoHash(idempotencyKey, hash)
                .map(this::toDomain);
    }

    @Override
    public CargaIdempotente save(CargaIdempotente carga) {
        CargaIdempotenteEntity entity = toEntity(carga);
        entity = cargaIdempotenteJpaRepository.save(entity);
        return toDomain(entity);
    }

    public CargaIdempotenteEntity toEntity(CargaIdempotente domain) {
        CargaIdempotenteEntity entity = new CargaIdempotenteEntity();
        entity.setId(domain.getId() != null ? domain.getId() : UUID.randomUUID());
        entity.setIdempotencyKey(domain.getIdempotencyKey());
        entity.setArchivoHash(domain.getArchivoHash());
        entity.setResultadoJson(domain.getResultadoJson());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public CargaIdempotente toDomain(CargaIdempotenteEntity entity) {
        return new CargaIdempotente(
                entity.getId(),
                entity.getIdempotencyKey(),
                entity.getArchivoHash(),
                entity.getResultadoJson(),
                entity.getCreatedAt()
        );
    }
}