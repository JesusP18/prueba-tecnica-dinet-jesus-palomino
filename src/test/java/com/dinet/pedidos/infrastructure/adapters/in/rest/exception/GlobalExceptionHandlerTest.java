package com.dinet.pedidos.infrastructure.adapters.in.rest.exception;

import com.dinet.pedidos.application.exception.DuplicateLoadException;
import com.dinet.pedidos.application.model.ErrorResponse;
import com.dinet.pedidos.domain.exception.PedidoValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handlePedidoValidationException_ShouldReturnBadRequestWithErrorDetails() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-123";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            String errorCode = "FECHA_INVALIDA";
            String errorMessage = "La fecha de entrega no puede ser en el pasado";
            PedidoValidationException ex = new PedidoValidationException(errorCode, errorMessage);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePedidoValidationException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(errorCode, errorResponse.getCode());
            assertEquals(errorMessage, errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-456";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            String errorMessage = "Argumento inválido proporcionado";
            IllegalArgumentException ex = new IllegalArgumentException(errorMessage);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("ARGUMENTO_INVALIDO", errorResponse.getCode());
            assertEquals(errorMessage, errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleMissingRequestHeaderException_ShouldReturnBadRequest() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-789";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            String headerName = "idempotency-key";
            MissingRequestHeaderException ex = new MissingRequestHeaderException(headerName, null);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMissingRequestHeaderException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("HEADER_FALTANTE", errorResponse.getCode());
            assertEquals("Header requerido faltante: " + headerName, errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleMaxUploadSizeExceededException_ShouldReturnPayloadTooLarge() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-999";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(10485760L);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMaxUploadSizeExceededException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("ARCHIVO_DEMASIADO_GRANDE", errorResponse.getCode());
            assertEquals("El archivo excede el tamaño máximo permitido", errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleDuplicateLoadException_ShouldReturnConflict() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-111";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            String errorMessage = "Carga duplicada detectada";
            DuplicateLoadException ex = new DuplicateLoadException(errorMessage);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDuplicateLoad(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("CARGA_DUPLICADA", errorResponse.getCode());
            assertEquals(errorMessage, errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            String correlationId = "test-correlation-222";
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(correlationId);

            String errorMessage = "Error inesperado";
            Exception ex = new Exception(errorMessage);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("ERROR_INTERNO", errorResponse.getCode());
            assertEquals("Error interno del servidor", errorResponse.getMessage());
            assertEquals(correlationId, errorResponse.getCorrelationId());
        }
    }

    @Disabled
    @Test
    void handleException_WhenCorrelationIdIsNull_ShouldGenerateNewCorrelationId() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class);
             MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {

            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(null);

            UUID mockUuid = UUID.fromString("12345678-1234-1234-1234-123456789012");
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            PedidoValidationException ex = new PedidoValidationException("TEST_ERROR", "Test error");

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handlePedidoValidationException(ex);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("gen-12345678-1234-1234-1234-123456789012", errorResponse.getCorrelationId());
        }
    }

    @Disabled
    @Test
    void handleException_WhenCorrelationIdIsEmpty_ShouldGenerateNewCorrelationId() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class);
             MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {

            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn("");

            UUID mockUuid = UUID.fromString("12345678-1234-1234-1234-123456789012");
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            IllegalArgumentException ex = new IllegalArgumentException("Test error");

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

            // Assert
            assertNotNull(response);

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("gen-12345678-1234-1234-1234-123456789012", errorResponse.getCorrelationId());
        }
    }

    @Disabled
    @Test
    void handleException_WhenCorrelationIdIsBlank_ShouldGenerateNewCorrelationId() {
        // Arrange
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class);
             MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {

            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn("   ");

            UUID mockUuid = UUID.fromString("12345678-1234-1234-1234-123456789012");
            mockedUuid.when(UUID::randomUUID).thenReturn(mockUuid);

            MissingRequestHeaderException ex = new MissingRequestHeaderException("test-header", null);

            // Act
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMissingRequestHeaderException(ex);

            // Assert
            assertNotNull(response);

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals("gen-12345678-1234-1234-1234-123456789012", errorResponse.getCorrelationId());
        }
    }

    @Test
    void getCorrelationId_WhenNull_ShouldGenerateNewCorrelationId() {
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn(null);

            try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class, CALLS_REAL_METHODS)) {
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
                mockedUuid.when(UUID::randomUUID).thenReturn(uuid);

                String result = globalExceptionHandler.getCorrelationId();
                assertEquals("gen-00000000-0000-0000-0000-000000000001", result);
            }
        }
    }

    @Test
    void getCorrelationId_WhenEmpty_ShouldGenerateNewCorrelationId() {
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn("");

            try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class, CALLS_REAL_METHODS)) {
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
                mockedUuid.when(UUID::randomUUID).thenReturn(uuid);

                String result = globalExceptionHandler.getCorrelationId();
                assertEquals("gen-00000000-0000-0000-0000-000000000002", result);
            }
        }
    }

    @Test
    void getCorrelationId_WhenBlank_ShouldGenerateNewCorrelationId() {
        try (MockedStatic<MDC> mockedMdc = mockStatic(MDC.class)) {
            mockedMdc.when(() -> MDC.get("correlationId")).thenReturn("   ");

            try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class, CALLS_REAL_METHODS)) {
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000003");
                mockedUuid.when(UUID::randomUUID).thenReturn(uuid);

                String result = globalExceptionHandler.getCorrelationId();
                assertEquals("gen-00000000-0000-0000-0000-000000000003", result);
            }
        }
    }

}