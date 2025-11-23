package com.dinet.pedidos.application.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DuplicateLoadExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String errorMessage = "Test error message";
        DuplicateLoadException exception = new DuplicateLoadException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String errorMessage = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        DuplicateLoadException exception = new DuplicateLoadException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionIsRuntimeException() {
        DuplicateLoadException exception = new DuplicateLoadException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}