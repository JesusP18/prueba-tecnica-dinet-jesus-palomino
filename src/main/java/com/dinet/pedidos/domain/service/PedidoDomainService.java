package com.dinet.pedidos.domain.service;

import com.dinet.pedidos.domain.exception.PedidoValidationException;
import com.dinet.pedidos.domain.model.Cliente;
import com.dinet.pedidos.domain.model.EstadoPedido;
import com.dinet.pedidos.domain.model.Pedido;
import com.dinet.pedidos.domain.model.Zona;
import com.dinet.pedidos.domain.ports.ClienteRepositoryPort;
import com.dinet.pedidos.domain.ports.ZonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

/**
 * Servicio de dominio: validaciones puras del negocio para Pedido.
 * No realiza llamadas a infraestructuras; recibe objetos del dominio ya resueltos.
 */
@RequiredArgsConstructor
@Service
public class PedidoDomainService {

    private static final Pattern NUMERO_PEDIDO_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    private final ClienteRepositoryPort clienteRepository;
    private final ZonaRepositoryPort zonaRepository;

    // Constante para la zona horaria requerida por la regla de negocio
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Lima");

    /**
     * Valida todas las reglas del dominio para un pedido.
     * Lanza PedidoValidationException con códigos específicos si alguna validación falla.
     *
     * @param pedido Pedido (dominio) a validar. Debe contener:
     *               - numeroPedido (String)
     *               - clienteId/cliente (Cliente) -> null = no existe en BD
     *               - zonaId/zona (Zona) -> null = no existe en BD
     *               - fechaEntrega (LocalDate)
     *               - estado (EstadoPedido)
     *               - requiereRefrigeracion (boolean)
     */
    public void validarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new PedidoValidationException("PEDIDO_NULO", "El pedido no puede ser nulo");
        }

        validarNumeroPedido(pedido.getNumeroPedido());
        validarEstado(pedido.getEstado());
        validarFechaEntrega(pedido.getFechaEntrega());

        validarClienteExistenteYActivo(pedido.getClienteId());
        validarZonaExistente(pedido.getZonaId());
        validarRequisitoCadenaFrio(pedido.isRequiereRefrigeracion(), pedido.getZonaId());
    }


    private void validarNumeroPedido(String numeroPedido) {
        if (numeroPedido == null || numeroPedido.isBlank()) {
            throw new PedidoValidationException("NUMERO_PEDIDO_INVALIDO", "numeroPedido es obligatorio");
        }
        if (!NUMERO_PEDIDO_PATTERN.matcher(numeroPedido).matches()) {
            throw new PedidoValidationException("NUMERO_PEDIDO_INVALIDO",
                    "numeroPedido debe ser alfanumérico (A-Z, a-z, 0-9) sin espacios ni símbolos");
        }
    }

    private void validarEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new PedidoValidationException("ESTADO_INVALIDO", "estado es obligatorio y debe ser PENDIENTE|CONFIRMADO|ENTREGADO");
        }
        // el enum ya limita valores, no hay más checks aquí
    }

    private void validarFechaEntrega(LocalDate fechaEntrega) {
        if (fechaEntrega == null) {
            throw new PedidoValidationException("FECHA_INVALIDA", "fechaEntrega es obligatoria");
        }

        // Usar la zona horaria de negocio (America/Lima) para comparar la fecha actual
        LocalDate hoy = LocalDate.now(BUSINESS_ZONE);
        if (fechaEntrega.isBefore(hoy)) {
            throw new PedidoValidationException("FECHA_INVALIDA", "fechaEntrega no puede ser anterior a la fecha actual (America/Lima)");
        }
    }

    /**
     * Comprueba que el cliente exista (cliente != null) y que esté activo.
     * La existencia física (consulta BD) debe hacerse en la capa de aplicación y pasar
     * el objeto Cliente ya resuelto. Si cliente == null => CLIENTE_NO_ENCONTRADO.
     */
    private void validarClienteExistenteYActivo(String clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new PedidoValidationException("CLIENTE_NO_EXISTE", "El cliente no existe"));

        if (!cliente.isActivo()) {
            throw new PedidoValidationException("CLIENTE_INACTIVO", "El cliente no está activo");
        }
    }

    /**
     * Comprueba existencia de zona. La resolución (buscar en BD) se hace en la capa de aplicación.
     */
    private void validarZonaExistente(String zonaId) {
        zonaRepository.findById(zonaId)
                .orElseThrow(() -> new PedidoValidationException("ZONA_NO_EXISTE", "La zona no existe"));
    }

    /**
     * Si requiere refrigeración, la zona debe soportarla.
     */
    private void validarRequisitoCadenaFrio(boolean requiereRefrigeracion, String zonaId) {
        Zona zona = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new PedidoValidationException("ZONA_NO_EXISTE", "La zona no existe"));

        if (requiereRefrigeracion && !zona.isSoporteRefrigeracion()) {
            throw new PedidoValidationException(
                    "ZONA_NO_PERMITE_REFRIGERACION",
                    "Esta zona no admite pedidos con refrigeración"
            );
        }
    }
}
