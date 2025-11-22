package com.dinet.pedidos.application.exception;

public class DuplicateLoadException extends RuntimeException {
    public DuplicateLoadException(String message) {
        super(message);
    }

    public DuplicateLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

