package com.dinet.pedidos.application.service;

import com.dinet.pedidos.application.exception.DuplicateLoadException;
import com.dinet.pedidos.application.model.CargaPedidosResult;
import com.dinet.pedidos.application.model.ErrorProcesamiento;
import com.dinet.pedidos.domain.model.CargaIdempotente;
import com.dinet.pedidos.domain.model.EstadoPedido;
import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.ports.CargaIdempotenteRepositoryPort;
import com.dinet.pedidos.domain.ports.PedidoRepositoryPort;
import com.dinet.pedidos.domain.service.PedidoDomainService;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargarPedidosServiceTest {

    @Mock
    private PedidoDomainService pedidoDomainService;

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private CargaIdempotenteRepositoryPort cargaIdempotenteRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private CargarPedidosService cargarPedidosService;

    @Captor
    private ArgumentCaptor<List<Pedido>> pedidosCaptor;

    @Captor
    private ArgumentCaptor<CargaIdempotente> cargaIdempotenteCaptor;

    private static final String IDEMPOTENCY_KEY = "test-key-123";
    private static final String VALID_CSV_CONTENT =
            "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                    "PED001,CLI001,2024-12-31,PENDIENTE,ZONA_NORTE,true\n" +
                    "PED002,CLI002,2024-12-25,ENTREGADO,ZONA_SUR,false";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cargarPedidosService, "batchSize", 500);
    }

    @Test
    void cargarPedidos_WhenValidFileAndNoDuplicates_ShouldProcessSuccessfully() throws Exception {
        // Arrange
        setupFileMock(VALID_CSV_CONTENT);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(pedidoRepository.existsByNumeroPedido(anyString())).thenReturn(false);
        when(pedidoRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(cargaIdempotenteRepository.save(any(CargaIdempotente.class))).thenReturn(new CargaIdempotente());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalProcesados());
        assertEquals(2, result.getGuardados());
        assertEquals(0, result.getConError());
        assertTrue(result.getErrores().isEmpty());

        verify(pedidoRepository, times(1)).saveAll(pedidosCaptor.capture());
        verify(cargaIdempotenteRepository, times(1)).save(cargaIdempotenteCaptor.capture());

        List<Pedido> savedPedidos = pedidosCaptor.getValue();
        assertEquals(2, savedPedidos.size());
    }

    @Test
    void cargarPedidos_WhenDuplicateIdempotencyKey_ShouldThrowDuplicateLoadException() throws Exception {
        // Arrange
        // Simular file.getBytes() para evitar RuntimeException
        when(file.getBytes()).thenReturn("test-content".getBytes());
        CargaIdempotente existingCarga = new CargaIdempotente();
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.of(existingCarga));

        // Act & Assert
        assertThrows(DuplicateLoadException.class, () -> {
            cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);
        });

        verify(pedidoRepository, never()).saveAll(anyList());
        verify(cargaIdempotenteRepository, never()).save(any(CargaIdempotente.class));
    }

    @Disabled
    @Test
    void cargarPedidos_WhenDuplicateNumeroPedidoInDatabase_ShouldAddError() throws Exception {
        // Arrange
        String csvContent =
                "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                        "PED001,CLI001,2024-12-31,PENDIENTE,ZONA_NORTE,true\n" +
                        "PED002,CLI002,2024-12-25,ENTREGADO,ZONA_SUR,false";

        setupFileMock(csvContent);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Configurar existsByNumeroPedido para retornar true solo para PED001
        when(pedidoRepository.existsByNumeroPedido("PED001")).thenReturn(true);
        when(pedidoRepository.existsByNumeroPedido("PED002")).thenReturn(false);

        when(pedidoRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(cargaIdempotenteRepository.save(any(CargaIdempotente.class))).thenReturn(new CargaIdempotente());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalProcesados());
        assertEquals(1, result.getGuardados()); // Solo PED002 se guarda
        assertEquals(1, result.getConError()); // PED001 tiene error

        assertEquals(1, result.getErrores().size());
        ErrorProcesamiento error = result.getErrores().get(0);
        assertTrue(error.getMotivo().contains("ya existe en la base de datos"));
        assertEquals("DUPLICADO", error.getErrorCode());
    }

    @Disabled
    @Test
    void cargarPedidos_WhenDuplicateNumeroPedidoInFile_ShouldAddError() throws Exception {
        // Arrange
        String csvWithDuplicates =
                "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                        "PED001,CLI001,2024-12-31,PENDIENTE,ZONA_NORTE,true\n" +
                        "PED001,CLI002,2024-12-25,ENTREGADO,ZONA_SUR,false"; // Duplicado

        setupFileMock(csvWithDuplicates);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(pedidoRepository.existsByNumeroPedido(anyString())).thenReturn(false);
        when(pedidoRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(cargaIdempotenteRepository.save(any(CargaIdempotente.class))).thenReturn(new CargaIdempotente());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalProcesados());
        assertEquals(1, result.getGuardados()); // Solo el primero se guarda
        assertEquals(1, result.getConError()); // El duplicado tiene error

        assertEquals(1, result.getErrores().size());
        ErrorProcesamiento error = result.getErrores().get(0);
        assertTrue(error.getMotivo().contains("duplicado en el archivo"));
        assertEquals("DUPLICADO_EN_ARCHIVO", error.getErrorCode());
    }

    @Test
    void cargarPedidos_WhenValidationException_ShouldAddError() throws Exception {
        // Arrange
        setupFileMock(VALID_CSV_CONTENT);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // No mockear existsByNumeroPedido ya que no se llega a llamar debido a la validación

        // Simular error de validación en el dominio para todos los pedidos
        doThrow(new RuntimeException("Error de validación"))
                .when(pedidoDomainService).validarPedido(any(Pedido.class));

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalProcesados());
        assertEquals(0, result.getGuardados());
        assertEquals(2, result.getConError());
        assertEquals(2, result.getErrores().size());

        // Verificar que no se guardó nada
        verify(pedidoRepository, never()).saveAll(anyList());
        verify(cargaIdempotenteRepository, never()).save(any(CargaIdempotente.class));
    }

    @Test
    void cargarPedidos_WhenInvalidDateFormat_ShouldAddError() throws Exception {
        // Arrange
        String csvWithInvalidDate =
                "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                        "PED001,CLI001,2024-13-45,PENDIENTE,ZONA_NORTE,true"; // Fecha inválida

        setupFileMock(csvWithInvalidDate);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalProcesados());
        assertEquals(0, result.getGuardados());
        assertEquals(1, result.getConError());

        ErrorProcesamiento error = result.getErrores().get(0);
        assertTrue(error.getMotivo().contains("Formato de fecha inválido"));
    }

    @Test
    void cargarPedidos_WhenInvalidEstado_ShouldAddError() throws Exception {
        // Arrange
        String csvWithInvalidEstado =
                "numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n" +
                        "PED001,CLI001,2024-12-31,ESTADO_INVALIDO,ZONA_NORTE,true"; // Estado inválido

        setupFileMock(csvWithInvalidEstado);
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalProcesados());
        assertEquals(0, result.getGuardados());
        assertEquals(1, result.getConError());

        ErrorProcesamiento error = result.getErrores().get(0);
        assertTrue(error.getMotivo().contains("Estado inválido"));
    }

    @Test
    void cargarPedidos_WhenBatchSizeIsSmall_ShouldProcessInSingleBatch() throws Exception {
        // Arrange
        ReflectionTestUtils.setField(cargarPedidosService, "batchSize", 1); // Será forzado a 500 por el Math.max

        StringBuilder largeCsv = new StringBuilder();
        largeCsv.append("numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion\n");
        for (int i = 1; i <= 3; i++) {
            largeCsv.append(String.format("PED%03d,CLI%03d,2024-12-31,PENDIENTE,ZONA_%d,false\n", i, i, i));
        }

        setupFileMock(largeCsv.toString());
        when(cargaIdempotenteRepository.findByIdempotencyKeyAndHash(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(pedidoRepository.existsByNumeroPedido(anyString())).thenReturn(false);
        when(pedidoRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(cargaIdempotenteRepository.save(any(CargaIdempotente.class))).thenReturn(new CargaIdempotente());

        // Act
        CargaPedidosResult result = cargarPedidosService.cargarPedidos(file, IDEMPOTENCY_KEY);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalProcesados());
        assertEquals(3, result.getGuardados());
        assertEquals(0, result.getConError());

        // Debería llamar saveAll 1 vez porque el batchSize mínimo es 500 (3 < 500)
        verify(pedidoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void calculateFileHash_ShouldReturnConsistentHash() throws Exception {
        // Arrange
        String testContent = "test content";
        when(file.getBytes()).thenReturn(testContent.getBytes());

        // Act
        String hash1 = cargarPedidosService.calculateFileHash(file);
        String hash2 = cargarPedidosService.calculateFileHash(file);

        // Assert
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(64, hash1.length()); // SHA-256 produces 64 hex characters
        assertEquals(hash1, hash2); // Should be consistent
    }

    @Disabled
    @Test
    void parsePedidoFromRecord_WithValidRecord_ShouldReturnPedido() {
        // Arrange
        // Crear instancia sin mocks para evitar stubbings innecesarios
        CargarPedidosService service = new CargarPedidosService(null, null, null);

        Map<String, String> recordData = new HashMap<>();
        recordData.put("numeroPedido", "TEST123");
        recordData.put("clienteId", "CLIENT456");
        recordData.put("fechaEntrega", "2024-12-31");
        recordData.put("estado", "PENDIENTE");
        recordData.put("zonaEntrega", "ZONA_TEST");
        recordData.put("requiereRefrigeracion", "true");

        CSVRecord record = mock(CSVRecord.class);
        when(record.toMap()).thenReturn(recordData);
        when(record.isMapped(anyString())).thenReturn(true);
        when(record.get("numeroPedido")).thenReturn("TEST123");
        when(record.get("clienteId")).thenReturn("CLIENT456");
        when(record.get("fechaEntrega")).thenReturn("2024-12-31");
        when(record.get("estado")).thenReturn("PENDIENTE");
        when(record.get("zonaEntrega")).thenReturn("ZONA_TEST");
        when(record.get("requiereRefrigeracion")).thenReturn("true");

        // Act
        Pedido pedido = service.parsePedidoFromRecord(record);

        // Assert
        assertNotNull(pedido);
        assertEquals("TEST123", pedido.getNumeroPedido());
        assertEquals("CLIENT456", pedido.getClienteId());
        assertEquals(LocalDate.of(2024, 12, 31), pedido.getFechaEntrega());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals("ZONA_TEST", pedido.getZonaId());
        assertTrue(pedido.isRequiereRefrigeracion());
    }

    @Disabled
    @Test
    void getField_WithNormalizedHeaders_ShouldFindField() {
        // Arrange
        CargarPedidosService service = new CargarPedidosService(null, null, null);

        Map<String, String> recordData = new HashMap<>();
        recordData.put("\uFEFFnumeroPedido", "TEST123"); // Header with BOM
        recordData.put("clienteId", "CLIENT456");
        recordData.put("fechaEntrega", "2024-12-31");
        recordData.put("estado", "PENDIENTE");
        recordData.put("zonaEntrega", "ZONA_TEST");
        recordData.put("requiereRefrigeracion", "true");

        CSVRecord record = mock(CSVRecord.class);
        when(record.toMap()).thenReturn(recordData);
        when(record.isMapped(anyString())).thenReturn(false);

        // Configurar el record para que tenga valores en los índices por si necesita fallback
        when(record.get(0)).thenReturn("TEST123");
        when(record.get(1)).thenReturn("CLIENT456");
        when(record.get(2)).thenReturn("2024-12-31");
        when(record.get(3)).thenReturn("PENDIENTE");
        when(record.get(4)).thenReturn("ZONA_TEST");
        when(record.get(5)).thenReturn("true");

        // Act & Assert - Verificar que no lanza excepción y puede parsear correctamente
        assertDoesNotThrow(() -> {
            Pedido pedido = service.parsePedidoFromRecord(record);
            assertNotNull(pedido);
            assertEquals("TEST123", pedido.getNumeroPedido());
        });
    }

    private void setupFileMock(String content) throws Exception {
        byte[] contentBytes = content.getBytes();
        InputStream inputStream = new ByteArrayInputStream(contentBytes);

        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getBytes()).thenReturn(contentBytes);
    }
}