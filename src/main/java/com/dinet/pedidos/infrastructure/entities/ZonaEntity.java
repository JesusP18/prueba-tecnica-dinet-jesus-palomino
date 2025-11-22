package com.dinet.pedidos.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "zonas")
public class ZonaEntity {
    @Id
    private String id;

    @Column(name = "soporte_refrigeracion", nullable = false)
    private boolean soporteRefrigeracion;

}