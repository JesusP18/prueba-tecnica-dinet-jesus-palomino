package com.dinet.pedidos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CargaIdempotente {
    private UUID id;
    private String idempotencyKey;
    private String archivoHash;
    private String resultadoJson;
    private LocalDateTime createdAt;

}