package com.dinet.pedidos.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CargaPedidosResult {
    private int totalProcesados;
    private int guardados;
    private int conError;
    private List<ErrorProcesamiento> errores = new ArrayList<>();

    public void agregarError(ErrorProcesamiento errorProcesamiento) {
        this.errores.add(errorProcesamiento);
        this.conError = this.errores.size();
    }

}