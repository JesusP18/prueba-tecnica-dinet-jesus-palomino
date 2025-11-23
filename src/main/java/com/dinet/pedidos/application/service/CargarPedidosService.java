package com.dinet.pedidos.application.service;

import com.dinet.pedidos.application.exception.DuplicateLoadException;
import com.dinet.pedidos.application.model.CargaPedidosResult;
import com.dinet.pedidos.application.model.ErrorProcesamiento;
import com.dinet.pedidos.application.ports.in.CargarPedidosUseCase;
import com.dinet.pedidos.domain.model.CargaIdempotente;
import com.dinet.pedidos.domain.model.EstadoPedido;
import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.ports.CargaIdempotenteRepositoryPort;
import com.dinet.pedidos.domain.ports.PedidoRepositoryPort;
import com.dinet.pedidos.domain.service.PedidoDomainService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CargarPedidosService implements CargarPedidosUseCase {

    private final PedidoDomainService pedidoDomainService;
    private final PedidoRepositoryPort pedidoRepository;
    private final CargaIdempotenteRepositoryPort cargaIdempotenteRepository;

    @Value("${app.batch.size:500}")
    private int batchSize;

    // Tamaño por defecto eliminado en favor de la propiedad

    @Override
    @Transactional
    public CargaPedidosResult cargarPedidos(MultipartFile file, String idempotencyKey) {
        // Calcular hash del archivo
        String fileHash = calculateFileHash(file);

        // Verificar idempotencia
        var existingCarga = cargaIdempotenteRepository.findByIdempotencyKeyAndHash(idempotencyKey, fileHash);
        if (existingCarga.isPresent()) {
            // Lanzar excepción específica para carga duplicada
            throw new DuplicateLoadException("Carga duplicada");
        }

        CargaPedidosResult result = new CargaPedidosResult();
        List<Pedido> pedidosValidos = new ArrayList<>();
        Set<String> seenNumeroPedidos = new HashSet<>(); // para detectar duplicados en el archivo

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            int lineNumber = 1; // La primera línea es el header, pero el parser ya la omite

            for (CSVRecord record : csvRecords) {
                lineNumber++;
                try {
                    Pedido pedido = parsePedidoFromRecord(record);
                    // Validaciones de dominio
                    pedidoDomainService.validarPedido(pedido);

                    String numeroPedido = pedido.getNumeroPedido();

                    // Validar duplicado dentro del mismo archivo
                    if (seenNumeroPedidos.contains(numeroPedido)) {
                        result.agregarError(new ErrorProcesamiento(lineNumber, "Numero de pedido duplicado en el archivo: " + numeroPedido, "DUPLICADO_EN_ARCHIVO"));
                        continue;
                    }

                    // Validar duplicado en la base de datos
                    if (pedidoRepository.existsByNumeroPedido(numeroPedido)) {
                        result.agregarError(new ErrorProcesamiento(lineNumber, "Numero de pedido ya existe en la base de datos: " + numeroPedido, "DUPLICADO"));
                        continue;
                    }

                    // Si pasa validaciones, marcar como visto y añadir a lista a persistir
                    seenNumeroPedidos.add(numeroPedido);
                    pedidosValidos.add(pedido);

                } catch (Exception e) {
                    // Capturar excepciones de validación y agregar al resultado
                    String errorCode = (e instanceof com.dinet.pedidos.domain.exception.PedidoValidationException)
                            ? ((com.dinet.pedidos.domain.exception.PedidoValidationException) e).getErrorCode()
                            : "ERROR_DESCONOCIDO";
                    result.agregarError(new ErrorProcesamiento(lineNumber, e.getMessage(), errorCode));
                }
            }

            // Ajustar totales antes de decidir persistir
            int totalProcesados = Math.max(0, lineNumber - 1);
            result.setTotalProcesados(totalProcesados);
            result.setGuardados(0);
            result.setConError(result.getErrores().size());

            // Si hubo errores, NO persistir nada: devolver el resultado con los errores
            if (!result.getErrores().isEmpty()) {
                return result;
            }

            // Validar rango permitido y usar batchSize configurado
            int effectiveBatchSize = Math.max(500, Math.min(batchSize, 1000));

            // Guardar en lotes sólo si no hubo errores
            int total = pedidosValidos.size();
            for (int i = 0; i < total; i += effectiveBatchSize) {
                List<Pedido> lote = pedidosValidos.subList(i, Math.min(total, i + effectiveBatchSize));
                pedidoRepository.saveAll(lote);
            }

            result.setGuardados(pedidosValidos.size());
            result.setConError(result.getErrores().size());

            // Guardar la carga idempotente
            CargaIdempotente carga = new CargaIdempotente();
            carga.setId(UUID.randomUUID());
            carga.setIdempotencyKey(idempotencyKey);
            carga.setArchivoHash(fileHash);
            // Serializar el resultado a JSON y guardarlo? Por ahora no lo hacemos.
            carga.setResultadoJson(null); // Podríamos usar Jackson para serializar el result a JSON
            carga.setCreatedAt(java.time.LocalDateTime.now());
            cargaIdempotenteRepository.save(carga);

        } catch (Exception e) {
            throw new DuplicateLoadException("Error al procesar el archivo", e);
        }

        return result;
    }

    public String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(file.getBytes());
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculando hash del archivo", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public Pedido parsePedidoFromRecord(CSVRecord record) {
        String numeroPedido = getField(record, "numeroPedido");
        String clienteId = getField(record, "clienteId");
        String fechaEntregaStr = getField(record, "fechaEntrega");
        String estadoStr = getField(record, "estado");
        String zonaEntrega = getField(record, "zonaEntrega");
        String requiereRefrigeracionStr = getField(record, "requiereRefrigeracion");

        LocalDate fechaEntrega;
        try {
            fechaEntrega = LocalDate.parse(fechaEntregaStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + fechaEntregaStr);
        }

        EstadoPedido estado = EstadoPedido.fromString(estadoStr);
        if (estado == null) {
            throw new IllegalArgumentException("Estado inválido: " + estadoStr);
        }

        boolean requiereRefrigeracion = Boolean.parseBoolean(requiereRefrigeracionStr);

        return Pedido.of(numeroPedido, clienteId, zonaEntrega, fechaEntrega, estado, requiereRefrigeracion);
    }

    /**
     * Devuelve el valor del campo intentando mapear nombres de cabecera con BOM/espacios y con
     * coincidencia case-insensitive. Si no encuentra el header por nombre, intenta por índice.
     */
    private String getField(CSVRecord record, String fieldName) {
        try {
            if (record.isMapped(fieldName)) {
                return record.get(fieldName);
            }
        } catch (IllegalArgumentException ignored) {
            // continue to try normalized keys
        }

        Map<String, String> map = record.toMap();
        for (String key : map.keySet()) {
            if (key == null) continue;
            String normalized = key.replace("\uFEFF", "").trim();
            if (normalized.equalsIgnoreCase(fieldName)) {
                return map.get(key);
            }
        }

        // Fallback por índice según el orden esperado
        try {
            switch (fieldName) {
                case "numeroPedido": return record.get(0);
                case "clienteId": return record.get(1);
                case "fechaEntrega": return record.get(2);
                case "estado": return record.get(3);
                case "zonaEntrega": return record.get(4);
                case "requiereRefrigeracion": return record.get(5);
                default: return record.get(fieldName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Mapping for " + fieldName + " not found, expected one of " + map.keySet());
        }
    }
}
