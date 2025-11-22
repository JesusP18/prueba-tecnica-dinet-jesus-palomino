package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.CargaIdempotente;

import java.util.Optional;

public interface CargaIdempotenteRepositoryPort {
    Optional<CargaIdempotente> findByIdempotencyKeyAndHash(String idempotencyKey, String hash);
    CargaIdempotente save(CargaIdempotente carga);
}