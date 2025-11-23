package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Zona;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZonaRepositoryPortTest {

    @Mock
    private ZonaRepositoryPort zonaRepositoryPort;

    @Test
    void findById_WithExistingId_ShouldReturnZona() {
        // Arrange
        String zonaId = "ZONA_NORTE";
        Zona expectedZona = new Zona(zonaId, true);

        when(zonaRepositoryPort.findById(zonaId))
                .thenReturn(Optional.of(expectedZona));

        // Act
        Optional<Zona> result = zonaRepositoryPort.findById(zonaId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedZona, result.get());
        assertEquals(zonaId, result.get().getId());
        assertTrue(result.get().isSoporteRefrigeracion());
        verify(zonaRepositoryPort).findById(zonaId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        String nonExistingId = "ZONA_INEXISTENTE";

        when(zonaRepositoryPort.findById(nonExistingId))
                .thenReturn(Optional.empty());

        // Act
        Optional<Zona> result = zonaRepositoryPort.findById(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
        verify(zonaRepositoryPort).findById(nonExistingId);
    }

    @Test
    void findById_WithNullId_ShouldReturnEmpty() {
        // Arrange
        when(zonaRepositoryPort.findById(null))
                .thenReturn(Optional.empty());

        // Act
        Optional<Zona> result = zonaRepositoryPort.findById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(zonaRepositoryPort).findById(null);
    }

    @Test
    void findById_WithZonaWithoutRefrigeracion_ShouldReturnZona() {
        // Arrange
        String zonaId = "ZONA_SUR";
        Zona zonaWithoutRefrigeracion = new Zona(zonaId, false);

        when(zonaRepositoryPort.findById(zonaId))
                .thenReturn(Optional.of(zonaWithoutRefrigeracion));

        // Act
        Optional<Zona> result = zonaRepositoryPort.findById(zonaId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(zonaId, result.get().getId());
        assertFalse(result.get().isSoporteRefrigeracion());
        verify(zonaRepositoryPort).findById(zonaId);
    }

    @Test
    void findById_WithDifferentZonaIds_ShouldReturnCorrectZonas() {
        // Arrange
        String zonaNorteId = "ZONA_NORTE";
        String zonaSurId = "ZONA_SUR";

        Zona zonaNorte = new Zona(zonaNorteId, true);
        Zona zonaSur = new Zona(zonaSurId, false);

        when(zonaRepositoryPort.findById(zonaNorteId)).thenReturn(Optional.of(zonaNorte));
        when(zonaRepositoryPort.findById(zonaSurId)).thenReturn(Optional.of(zonaSur));

        // Act
        Optional<Zona> resultNorte = zonaRepositoryPort.findById(zonaNorteId);
        Optional<Zona> resultSur = zonaRepositoryPort.findById(zonaSurId);

        // Assert
        assertTrue(resultNorte.isPresent());
        assertTrue(resultSur.isPresent());
        assertEquals(zonaNorteId, resultNorte.get().getId());
        assertEquals(zonaSurId, resultSur.get().getId());
        assertTrue(resultNorte.get().isSoporteRefrigeracion());
        assertFalse(resultSur.get().isSoporteRefrigeracion());

        verify(zonaRepositoryPort).findById(zonaNorteId);
        verify(zonaRepositoryPort).findById(zonaSurId);
    }
}