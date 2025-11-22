package com.dinet.pedidos.application.ports.in;

import com.dinet.pedidos.application.model.CargaPedidosResult;
import org.springframework.web.multipart.MultipartFile;

public interface CargarPedidosUseCase {
    CargaPedidosResult cargarPedidos(MultipartFile file, String idempotencyKey);
}