package com.dinet.pedidos.infrastructure.adapters.out.persistence;

import com.dinet.pedidos.domain.model.Cliente;
import com.dinet.pedidos.infrastructure.entities.ClienteEntity;
import com.dinet.pedidos.infrastructure.repositories.ClienteJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryAdapterTest {

    @Mock
    private ClienteJpaRepository clienteJpaRepository;

    @InjectMocks
    private ClienteRepositoryAdapter clienteRepositoryAdapter;

    @Test
    void findById_WhenClienteExists_ShouldReturnCliente() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId("CLI001");
        entity.setActivo(true);

        when(clienteJpaRepository.findById("CLI001")).thenReturn(Optional.of(entity));

        // Act
        Optional<Cliente> result = clienteRepositoryAdapter.findById("CLI001");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("CLI001");
        assertThat(result.get().isActivo()).isTrue();
    }

    @Test
    void findById_WhenClienteDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(clienteJpaRepository.findById("NON_EXISTENT")).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> result = clienteRepositoryAdapter.findById("NON_EXISTENT");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_WithInactiveCliente_ShouldReturnCliente() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId("CLI002");
        entity.setActivo(false);

        when(clienteJpaRepository.findById("CLI002")).thenReturn(Optional.of(entity));

        // Act
        Optional<Cliente> result = clienteRepositoryAdapter.findById("CLI002");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("CLI002");
        assertThat(result.get().isActivo()).isFalse();
    }

    @Test
    void toDomain_ShouldConvertEntityToDomainCorrectly() {
        // Arrange
        ClienteEntity entity = new ClienteEntity();
        entity.setId("CLI003");
        entity.setActivo(true);

        // Act
        Cliente domain = clienteRepositoryAdapter.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo("CLI003");
        assertThat(domain.isActivo()).isTrue();
    }
}