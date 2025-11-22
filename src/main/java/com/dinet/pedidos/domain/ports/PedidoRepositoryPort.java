package com.dinet.pedidos.domain.ports;

import com.dinet.pedidos.domain.model.Pedido;
import java.util.List;

public interface PedidoRepositoryPort {
    Pedido save(Pedido pedido);
    List<Pedido> saveAll(List<Pedido> pedidos);
    boolean existsByNumeroPedido(String numeroPedido);
}