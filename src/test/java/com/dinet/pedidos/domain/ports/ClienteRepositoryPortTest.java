package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Cliente;
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
class ClienteRepositoryPortTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Test
    void findById_WithExistingId_ShouldReturnCliente() {
        // Arrange
        String clienteId = "CLI001";
        Cliente expectedCliente = new Cliente(clienteId, true);

        when(clienteRepositoryPort.findById(clienteId))
                .thenReturn(Optional.of(expectedCliente));

        // Act
        Optional<Cliente> result = clienteRepositoryPort.findById(clienteId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedCliente, result.get());
        assertEquals(clienteId, result.get().getId());
        assertTrue(result.get().isActivo());
        verify(clienteRepositoryPort).findById(clienteId);
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        String nonExistingId = "NON_EXISTENT";

        when(clienteRepositoryPort.findById(nonExistingId))
                .thenReturn(Optional.empty());

        // Act
        Optional<Cliente> result = clienteRepositoryPort.findById(nonExistingId);

        // Assert
        assertFalse(result.isPresent());
        verify(clienteRepositoryPort).findById(nonExistingId);
    }

    @Test
    void findById_WithNullId_ShouldReturnEmpty() {
        // Arrange
        when(clienteRepositoryPort.findById(null))
                .thenReturn(Optional.empty());

        // Act
        Optional<Cliente> result = clienteRepositoryPort.findById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(clienteRepositoryPort).findById(null);
    }

    @Test
    void findById_WithInactiveCliente_ShouldReturnCliente() {
        // Arrange
        String clienteId = "CLI002";
        Cliente inactiveCliente = new Cliente(clienteId, false);

        when(clienteRepositoryPort.findById(clienteId))
                .thenReturn(Optional.of(inactiveCliente));

        // Act
        Optional<Cliente> result = clienteRepositoryPort.findById(clienteId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(clienteId, result.get().getId());
        assertFalse(result.get().isActivo());
        verify(clienteRepositoryPort).findById(clienteId);
    }
}