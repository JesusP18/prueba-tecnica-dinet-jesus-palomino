package com.dinet.pedidos.domain.service;

import com.dinet.pedidos.domain.exception.PedidoValidationException;
import com.dinet.pedidos.domain.model.Cliente;
import com.dinet.pedidos.domain.model.EstadoPedido;
import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.model.Zona;
import com.dinet.pedidos.domain.ports.ClienteRepositoryPort;
import com.dinet.pedidos.domain.ports.ZonaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoDomainServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ZonaRepositoryPort zonaRepository;

    private PedidoDomainService pedidoDomainService;

    @BeforeEach
    void setUp() {
        pedidoDomainService = new PedidoDomainService(clienteRepository, zonaRepository);
    }

    @Test
    void validarPedido_WithValidPedido_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        mockRepositoriesForValidPedido();

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @Test
    void validarPedido_WithNullPedido_ShouldThrowException() {
        // Arrange
        Pedido nullPedido = null;

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(nullPedido));

        assertEquals("PEDIDO_NULO", exception.getErrorCode());
        assertEquals("El pedido no puede ser nulo", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void validarPedido_WithInvalidNumeroPedido_ShouldThrowException(String invalidNumeroPedido) {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setNumeroPedido(invalidNumeroPedido);
        // NO mockear repositorios porque la validación falla antes de llegar a ellos

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("NUMERO_PEDIDO_INVALIDO", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("numeroPedido es obligatorio"));

        // Verificar que NO se llamó a los repositorios
        verifyNoInteractions(clienteRepository, zonaRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PED-001", "PED 001", "PED@001", "PED_001", "pedido#1"})
    void validarPedido_WithInvalidNumeroPedidoFormat_ShouldThrowException(String invalidNumeroPedido) {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setNumeroPedido(invalidNumeroPedido);
        // NO mockear repositorios porque la validación falla antes de llegar a ellos

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("NUMERO_PEDIDO_INVALIDO", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("alfanumérico"));

        // Verificar que NO se llamó a los repositorios
        verifyNoInteractions(clienteRepository, zonaRepository);
    }

    @Test
    void validarPedido_WithNullEstado_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setEstado(null);
        // NO mockear repositorios porque la validación falla antes de llegar a ellos

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("ESTADO_INVALIDO", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("estado es obligatorio"));

        // Verificar que NO se llamó a los repositorios
        verifyNoInteractions(clienteRepository, zonaRepository);
    }

    @Test
    void validarPedido_WithNullFechaEntrega_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setFechaEntrega(null);
        // NO mockear repositorios porque la validación falla antes de llegar a ellos

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("FECHA_INVALIDA", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("fechaEntrega es obligatoria"));

        // Verificar que NO se llamó a los repositorios
        verifyNoInteractions(clienteRepository, zonaRepository);
    }

    @Test
    void validarPedido_WithPastFechaEntrega_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setFechaEntrega(LocalDate.now().minusDays(1));
        // NO mockear repositorios porque la validación falla antes de llegar a ellos

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("FECHA_INVALIDA", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("no puede ser anterior a la fecha actual"));

        // Verificar que NO se llamó a los repositorios
        verifyNoInteractions(clienteRepository, zonaRepository);
    }

    @Test
    void validarPedido_WithNonExistingCliente_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        when(clienteRepository.findById("CLI001")).thenReturn(Optional.empty());

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("CLIENTE_NO_EXISTE", exception.getErrorCode());
        assertEquals("El cliente no existe", exception.getMessage());
    }

    @Test
    void validarPedido_WithInactiveCliente_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        Cliente inactiveCliente = new Cliente("CLI001", false);
        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(inactiveCliente));

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("CLIENTE_INACTIVO", exception.getErrorCode());
        assertEquals("El cliente no está activo", exception.getMessage());
    }

    @Test
    void validarPedido_WithNonExistingZona_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(new Cliente("CLI001", true)));
        when(zonaRepository.findById("ZONA_NORTE")).thenReturn(Optional.empty());

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("ZONA_NO_EXISTE", exception.getErrorCode());
        assertEquals("La zona no existe", exception.getMessage());
    }

    @Test
    void validarPedido_WithRefrigeracionButZonaWithoutSupport_ShouldThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setRequiereRefrigeracion(true);

        Cliente cliente = new Cliente("CLI001", true);
        Zona zonaWithoutRefrigeracion = new Zona("ZONA_NORTE", false);

        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(cliente));
        when(zonaRepository.findById("ZONA_NORTE")).thenReturn(Optional.of(zonaWithoutRefrigeracion));

        // Act & Assert
        PedidoValidationException exception = assertThrows(PedidoValidationException.class,
                () -> pedidoDomainService.validarPedido(pedido));

        assertEquals("ZONA_NO_PERMITE_REFRIGERACION", exception.getErrorCode());
        assertEquals("Esta zona no admite pedidos con refrigeración", exception.getMessage());
    }

    @Test
    void validarPedido_WithRefrigeracionAndZonaWithSupport_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setRequiereRefrigeracion(true);

        Cliente cliente = new Cliente("CLI001", true);
        Zona zonaWithRefrigeracion = new Zona("ZONA_NORTE", true);

        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(cliente));
        when(zonaRepository.findById("ZONA_NORTE")).thenReturn(Optional.of(zonaWithRefrigeracion));

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @Test
    void validarPedido_WithoutRefrigeracionAndZonaWithoutSupport_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setRequiereRefrigeracion(false);

        Cliente cliente = new Cliente("CLI001", true);
        Zona zonaWithoutRefrigeracion = new Zona("ZONA_NORTE", false);

        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(cliente));
        when(zonaRepository.findById("ZONA_NORTE")).thenReturn(Optional.of(zonaWithoutRefrigeracion));

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @Test
    void validarPedido_WithTodayFechaEntrega_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setFechaEntrega(LocalDate.now());
        mockRepositoriesForValidPedido();

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @Test
    void validarPedido_WithFutureFechaEntrega_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setFechaEntrega(LocalDate.now().plusDays(1));
        mockRepositoriesForValidPedido();

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PED001", "PED123", "ABC123", "123ABC", "abc123", "AbC123"})
    void validarPedido_WithValidNumeroPedidoFormats_ShouldNotThrowException(String validNumeroPedido) {
        // Arrange
        Pedido pedido = createValidPedido();
        pedido.setNumeroPedido(validNumeroPedido);
        mockRepositoriesForValidPedido();

        // Act & Assert
        assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido));
    }

    @Test
    void validarPedido_WithDifferentValidEstados_ShouldNotThrowException() {
        // Arrange
        Pedido pedido = createValidPedido();
        mockRepositoriesForValidPedido();

        // Test all valid estados
        for (EstadoPedido estado : EstadoPedido.values()) {
            pedido.setEstado(estado);

            // Act & Assert
            assertDoesNotThrow(() -> pedidoDomainService.validarPedido(pedido),
                    "Should not throw exception for estado: " + estado);
        }
    }

    private Pedido createValidPedido() {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("PED001");
        pedido.setClienteId("CLI001");
        pedido.setZonaId("ZONA_NORTE");
        pedido.setFechaEntrega(LocalDate.now().plusDays(1));
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setRequiereRefrigeracion(false);
        return pedido;
    }

    private void mockRepositoriesForValidPedido() {
        Cliente cliente = new Cliente("CLI001", true);
        Zona zona = new Zona("ZONA_NORTE", true);

        when(clienteRepository.findById("CLI001")).thenReturn(Optional.of(cliente));
        when(zonaRepository.findById("ZONA_NORTE")).thenReturn(Optional.of(zona));
    }
}