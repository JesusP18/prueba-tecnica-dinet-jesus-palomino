// PedidoEntityTest.java
package com.dinet.pedidos.infrastructure.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class PedidoEntityTest {

    @Test
    void shouldCreatePedidoEntity() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDate deliveryDate = LocalDate.now().plusDays(1);

        PedidoEntity entity = new PedidoEntity();
        entity.setId(id);
        entity.setNumeroPedido("PED-001");
        entity.setClienteId("CLI-001");
        entity.setZonaId("ZON-001");
        entity.setFechaEntrega(deliveryDate);
        entity.setEstado("PENDIENTE");
        entity.setRequiereRefrigeracion(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getNumeroPedido()).isEqualTo("PED-001");
        assertThat(entity.getClienteId()).isEqualTo("CLI-001");
        assertThat(entity.getZonaId()).isEqualTo("ZON-001");
        assertThat(entity.getFechaEntrega()).isEqualTo(deliveryDate);
        assertThat(entity.getEstado()).isEqualTo("PENDIENTE");
        assertThat(entity.isRequiereRefrigeracion()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldCreatePedidoEntityWithAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDate deliveryDate = LocalDate.now().plusDays(1);

        PedidoEntity entity = new PedidoEntity(
                id, "PED-001", "CLI-001", "ZON-001",
                deliveryDate, "PENDIENTE", true, now, now
        );

        assertThat(entity.getNumeroPedido()).isEqualTo("PED-001");
        assertThat(entity.isRequiereRefrigeracion()).isTrue();
        assertThat(entity.getEstado()).isEqualTo("PENDIENTE");
    }
}