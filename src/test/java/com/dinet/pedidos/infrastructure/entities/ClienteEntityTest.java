// ClienteEntityTest.java
package com.dinet.pedidos.infrastructure.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ClienteEntityTest {

    @Test
    void shouldCreateClienteEntity() {
        ClienteEntity entity = new ClienteEntity();
        entity.setId("CLI-001");
        entity.setActivo(true);

        assertThat(entity.getId()).isEqualTo("CLI-001");
        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    void shouldCreateClienteEntityWithAllArgsConstructor() {
        ClienteEntity entity = new ClienteEntity("CLI-001", true);

        assertThat(entity.getId()).isEqualTo("CLI-001");
        assertThat(entity.isActivo()).isTrue();
    }

    @Test
    void shouldHandleInactiveClient() {
        ClienteEntity entity = new ClienteEntity("CLI-002", false);

        assertThat(entity.isActivo()).isFalse();
    }
}