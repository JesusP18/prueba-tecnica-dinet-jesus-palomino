package com.dinet.pedidos.domain.exception;

/**
 * Excepción de validación del dominio para pedidos.
 * Incluye un errorCode (útil para agrupar/contabilizar errores en la respuesta).
 */
public class PedidoValidationException extends RuntimeException {

    private final String errorCode;

    public PedidoValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PedidoValidationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
