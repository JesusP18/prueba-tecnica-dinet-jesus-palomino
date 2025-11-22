package com.dinet.pedidos.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorProcesamiento {
    private int numeroLinea;
    private String motivo;
    private String errorCode;

}