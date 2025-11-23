// ZonaEntityTest.java
package com.dinet.pedidos.infrastructure.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ZonaEntityTest {

    @Test
    void shouldCreateZonaEntity() {
        ZonaEntity entity = new ZonaEntity();
        entity.setId("ZON-001");
        entity.setSoporteRefrigeracion(true);

        assertThat(entity.getId()).isEqualTo("ZON-001");
        assertThat(entity.isSoporteRefrigeracion()).isTrue();
    }

    @Test
    void shouldCreateZonaEntityWithAllArgsConstructor() {
        ZonaEntity entity = new ZonaEntity("ZON-001", true);

        assertThat(entity.getId()).isEqualTo("ZON-001");
        assertThat(entity.isSoporteRefrigeracion()).isTrue();
    }

    @Test
    void shouldHandleZoneWithoutRefrigeration() {
        ZonaEntity entity = new ZonaEntity("ZON-002", false);

        assertThat(entity.isSoporteRefrigeracion()).isFalse();
    }
}