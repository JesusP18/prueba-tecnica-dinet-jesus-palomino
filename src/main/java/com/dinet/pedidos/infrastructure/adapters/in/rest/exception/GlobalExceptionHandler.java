package com.dinet.pedidos.infrastructure.adapters.in.rest.exception;

import com.dinet.pedidos.application.exception.DuplicateLoadException;
import com.dinet.pedidos.application.model.ErrorResponse;
import com.dinet.pedidos.domain.exception.PedidoValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PedidoValidationException.class)
    public ResponseEntity<ErrorResponse> handlePedidoValidationException(PedidoValidationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(ex.getErrorCode());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("ARGUMENTO_INVALIDO");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("HEADER_FALTANTE");
        errorResponse.setMessage("Header requerido faltante: " + ex.getHeaderName());
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("ARCHIVO_DEMASIADO_GRANDE");
        errorResponse.setMessage("El archivo excede el tamaño máximo permitido");
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(DuplicateLoadException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoad(DuplicateLoadException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("CARGA_DUPLICADA");
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Error interno del servidor", ex);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("ERROR_INTERNO");
        errorResponse.setMessage("Error interno del servidor");
        errorResponse.setCorrelationId(getCorrelationId());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Obtiene el correlationId del MDC o genera uno nuevo si no existe
     */
    public String getCorrelationId() {
        String correlationId = MDC.get("correlationId");
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = "gen-" + UUID.randomUUID().toString();
            logger.warn("CorrelationId no encontrado en MDC, generando uno nuevo: {}", correlationId);
        }
        return correlationId;
    }
}