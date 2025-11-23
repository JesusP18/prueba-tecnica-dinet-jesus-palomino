package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.Zona;
import com.dinet.pedidos.infrastructure.entities.ZonaEntity;
import com.dinet.pedidos.infrastructure.repositories.ZonaJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZonaRepositoryAdapterTest {

    @Mock
    private ZonaJpaRepository zonaJpaRepository;

    @InjectMocks
    private ZonaRepositoryAdapter zonaRepositoryAdapter;

    @Test
    void findById_WhenZonaExists_ShouldReturnZona() {
        // Arrange
        ZonaEntity entity = new ZonaEntity();
        entity.setId("ZONA_NORTE");
        entity.setSoporteRefrigeracion(true);

        when(zonaJpaRepository.findById("ZONA_NORTE")).thenReturn(Optional.of(entity));

        // Act
        Optional<Zona> result = zonaRepositoryAdapter.findById("ZONA_NORTE");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("ZONA_NORTE");
        assertThat(result.get().isSoporteRefrigeracion()).isTrue();
    }

    @Test
    void findById_WhenZonaDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(zonaJpaRepository.findById("NON_EXISTENT")).thenReturn(Optional.empty());

        // Act
        Optional<Zona> result = zonaRepositoryAdapter.findById("NON_EXISTENT");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithZonaWithoutRefrigeracion_ShouldReturnZona() {
        // Arrange
        ZonaEntity entity = new ZonaEntity();
        entity.setId("ZONA_SUR");
        entity.setSoporteRefrigeracion(false);

        when(zonaJpaRepository.findById("ZONA_SUR")).thenReturn(Optional.of(entity));

        // Act
        Optional<Zona> result = zonaRepositoryAdapter.findById("ZONA_SUR");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("ZONA_SUR");
        assertThat(result.get().isSoporteRefrigeracion()).isFalse();
    }

    @Test
    void toDomain_ShouldConvertEntityToDomainCorrectly() {
        // Arrange
        ZonaEntity entity = new ZonaEntity();
        entity.setId("ZONA_TEST");
        entity.setSoporteRefrigeracion(true);

        // Act
        Zona domain = zonaRepositoryAdapter.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo("ZONA_TEST");
        assertThat(domain.isSoporteRefrigeracion()).isTrue();
    }
}