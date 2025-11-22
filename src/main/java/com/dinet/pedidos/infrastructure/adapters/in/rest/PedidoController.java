package com.dinet.pedidos.infrastructure.adapters.in.rest;

import com.dinet.pedidos.application.model.CargaPedidosResult;
import com.dinet.pedidos.application.ports.in.CargarPedidosUseCase;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Cargar pedidos desde CSV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carga exitosa"),
            @ApiResponse(responseCode = "400", description = "Error en datos"),
            @ApiResponse(responseCode = "409", description = "Carga duplicada"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping("/cargar")
    public ResponseEntity<CargaPedidosResult> cargarPedidos(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestParam("file") MultipartFile file) {
        CargaPedidosResult result = cargarPedidosUseCase.cargarPedidos(file, idempotencyKey);
        return ResponseEntity.ok(result);
    }
}