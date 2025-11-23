package com.dinet.pedidos.infrastructure.adapters.in.rest;

import com.dinet.pedidos.application.model.CargaPedidosResult;
import com.dinet.pedidos.application.ports.in.CargarPedidosUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Pedidos", description = "API para gestión de pedidos")
@RequiredArgsConstructor
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CargarPedidosUseCase cargarPedidosUseCase;

    @Operation(
            summary = "Cargar pedidos desde archivo CSV",
            description = "Endpoint para cargar múltiples pedidos desde un archivo CSV. Requiere autenticación Bearer Token y Idempotency-Key."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga exitosa"),
            @ApiResponse(responseCode = "400", description = "Error en datos"),
            @ApiResponse(responseCode = "409", description = "Carga duplicada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping(value = "/cargar", consumes = "multipart/form-data")
    public ResponseEntity<CargaPedidosResult> cargarPedidos(
            @Parameter(
                    description = "Archivo CSV con los pedidos",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart("file") MultipartFile file,

            @Parameter(
                    description = "Clave de idempotencia para evitar duplicados",
                    required = true,
                    example = "test-123"
            )
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        CargaPedidosResult result = cargarPedidosUseCase.cargarPedidos(file, idempotencyKey);
        return ResponseEntity.ok(result);
    }
}