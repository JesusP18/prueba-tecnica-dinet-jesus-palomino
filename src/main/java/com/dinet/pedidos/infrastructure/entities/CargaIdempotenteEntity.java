package com.dinet.pedidos.infrastructure.entities;

import jakarta.persistence.*;
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
@Entity
@Table(name = "cargas_idempotencia")
public class CargaIdempotenteEntity {
    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "archivo_hash", nullable = false)
    private String archivoHash;

    @Column(name = "resultado_json", columnDefinition = "text")
    private String resultadoJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}