package com.dinet.pedidos.infrastructure.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();
    private static final String HEADER = "X-Correlation-Id";

    @AfterEach
    void cleanUp() {
        MDC.clear();
    }

    // ==============================================
    // 1. Cuando el header YA VIENE en la petición
    // ==============================================
    @Test
    void doFilter_ShouldUseExistingCorrelationId_WhenHeaderPresent() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String existingId = "38b7363a-c7cd-43c7-abdb-e7f3120340b9";
        request.addHeader("X-Correlation-Id", existingId);

        FilterChain chain = mock(FilterChain.class);

        // Act
        filter.doFilterInternal(request, response, chain);

        // Assert
        assertEquals(existingId, response.getHeader("X-Correlation-Id"));
        verify(chain, times(1)).doFilter(request, response);
    }

    // ==============================================
    // 2. Cuando NO VIENE el header → genera UUID
    // ==============================================
    @Test
    void doFilter_ShouldGenerateNewCorrelationId_WhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        String id = response.getHeader(HEADER);

        assertNotNull(id);
        assertFalse(id.isBlank());
        assertDoesNotThrow(() -> UUID.fromString(id)); // valida formato UUID

        // validar que se guardó en MDC durante la ejecución
        assertNull(MDC.get("correlationId")); // limpio después del finally
    }

    // ==============================================
    // 3. Cuando el header es BLANK → genera UUID
    // ==============================================
    @Test
    void doFilter_ShouldGenerateCorrelationId_WhenHeaderBlank() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER, "   "); // blank

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        String id = response.getHeader(HEADER);

        assertNotNull(id);
        assertFalse(id.isBlank());
        assertDoesNotThrow(() -> UUID.fromString(id));

        // MDC debe estar limpio después del filtro
        assertNull(MDC.get("correlationId"));
    }

    // ==============================================
    // 4. Verificar que siempre se ejecuta el filterChain
    // ==============================================
    @Test
    void doFilter_ShouldInvokeFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        // Si chain se ejecutó, debería haber un request procesado
        assertNotNull(chain.getRequest());
        assertNotNull(chain.getResponse());
    }
}
