package com.dinet.pedidos.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ZonaTest {

    @Test
    @DisplayName("Debería crear instancia con valores por defecto")
    void shouldCreateInstanceWithDefaultValues() {
        Zona zona = new Zona();

        assertNull(zona.getId());
        assertFalse(zona.isSoporteRefrigeracion());
    }

    @Test
    @DisplayName("Debería crear instancia con todos los argumentos")
    void shouldCreateInstanceWithAllArgsConstructor() {
        Zona zona = new Zona("ZONA_NORTE", true);

        assertEquals("ZONA_NORTE", zona.getId());
        assertTrue(zona.isSoporteRefrigeracion());
    }

    @Test
    @DisplayName("Debería permitir modificar valores con setters")
    void shouldAllowModifyingValuesWithSetters() {
        Zona zona = new Zona();

        zona.setId("ZONA_SUR");
        zona.setSoporteRefrigeracion(false);

        assertEquals("ZONA_SUR", zona.getId());
        assertFalse(zona.isSoporteRefrigeracion());
    }

    @Test
    @DisplayName("Debería considerar iguales dos zonas con el mismo ID")
    void shouldConsiderEqualZonesWithSameId() {
        Zona zona1 = new Zona("ZONA_NORTE", true);
        Zona zona2 = new Zona("ZONA_NORTE", false); // Diferente soporte refrigeración

        assertEquals(zona1, zona2);
        assertEquals(zona1.hashCode(), zona2.hashCode());
    }

    @Test
    @DisplayName("Debería considerar diferentes dos zonas con diferente ID")
    void shouldConsiderDifferentZonesWithDifferentId() {
        Zona zona1 = new Zona("ZONA_NORTE", true);
        Zona zona2 = new Zona("ZONA_SUR", true);

        assertNotEquals(zona1, zona2);
        assertNotEquals(zona1.hashCode(), zona2.hashCode());
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con null")
    void shouldReturnFalseWhenComparedWithNull() {
        Zona zona = new Zona("ZONA_NORTE", true);

        assertNotEquals(null, zona);
    }

    @Test
    @DisplayName("Debería retornar false cuando se compara con objeto de diferente tipo")
    void shouldReturnFalseWhenComparedWithDifferentType() {
        Zona zona = new Zona("ZONA_NORTE", true);

        assertNotEquals("ZONA_NORTE", zona);
    }

    @Test
    @DisplayName("Debería ser igual a sí mismo")
    void shouldBeEqualToItself() {
        Zona zona = new Zona("ZONA_NORTE", true);

        assertEquals(zona, zona);
    }
}