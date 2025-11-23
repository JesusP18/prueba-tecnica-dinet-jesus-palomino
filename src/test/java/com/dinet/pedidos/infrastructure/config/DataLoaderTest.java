package com.dinet.pedidos.infrastructure.config;

import com.dinet.pedidos.infrastructure.entities.ClienteEntity;
import com.dinet.pedidos.infrastructure.entities.ZonaEntity;
import com.dinet.pedidos.infrastructure.repositories.ClienteJpaRepository;
import com.dinet.pedidos.infrastructure.repositories.ZonaJpaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataLoaderTest {

    @Test
    void loadData_ShouldInsertClientsAndZones_WhenRepositoriesAreEmpty() throws Exception {
        // Arrange
        ClienteJpaRepository clienteRepo = mock(ClienteJpaRepository.class);
        ZonaJpaRepository zonaRepo = mock(ZonaJpaRepository.class);

        when(clienteRepo.count()).thenReturn(0L);
        when(zonaRepo.count()).thenReturn(0L);

        DataLoader loader = new DataLoader();

        // Act
        loader.loadData(clienteRepo, zonaRepo).run();

        // Assert — verify client inserts
        ArgumentCaptor<ClienteEntity> clienteCaptor = ArgumentCaptor.forClass(ClienteEntity.class);
        verify(clienteRepo, times(5)).save(clienteCaptor.capture());
        List<ClienteEntity> clientesInsertados = clienteCaptor.getAllValues();
        assertEquals(5, clientesInsertados.size());

        // Assert — verify zone inserts
        ArgumentCaptor<ZonaEntity> zonaCaptor = ArgumentCaptor.forClass(ZonaEntity.class);
        verify(zonaRepo, times(5)).save(zonaCaptor.capture());
        List<ZonaEntity> zonasInsertadas = zonaCaptor.getAllValues();
        assertEquals(5, zonasInsertadas.size());

        // Verify logs no son necesarios para cobertura
        verify(clienteRepo).count();
        verify(zonaRepo).count();
    }

    @Test
    void loadData_ShouldNotInsertAnything_WhenDataAlreadyExists() throws Exception {
        // Arrange
        ClienteJpaRepository clienteRepo = mock(ClienteJpaRepository.class);
        ZonaJpaRepository zonaRepo = mock(ZonaJpaRepository.class);

        when(clienteRepo.count()).thenReturn(10L);
        when(zonaRepo.count()).thenReturn(7L);

        DataLoader loader = new DataLoader();

        // Act
        loader.loadData(clienteRepo, zonaRepo).run();

        // Assert — verify NO inserts happened
        verify(clienteRepo, never()).save(any());
        verify(zonaRepo, never()).save(any());

        verify(clienteRepo).count();
        verify(zonaRepo).count();
    }
}
