package com.dinet.pedidos.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    // =======================
    //  CORS
    // =======================
    @Test
    void corsConfigurationSource_ShouldAllowAllOriginsAndMethods() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        // FIX: usar request mock, no null
        MockHttpServletRequest request = new MockHttpServletRequest();

        CorsConfiguration cors = source.getCorsConfiguration(request);

        assertNotNull(cors);
        assertEquals(List.of("*"), cors.getAllowedOrigins());
        assertTrue(cors.getAllowedMethods().contains("GET"));
    }

    // =======================
    //  JWT – Real Access
    // =======================
    @Test
    void jwtAuthenticationConverter_ShouldExtractRealmAccessRoles() {
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

        Map<String, Object> realmAccess = Map.of("roles", List.of("admin", "viewer"));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", realmAccess)
                .build();

        JwtAuthenticationToken auth = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // =======================
    //  JWT – Client Access
    // =======================
    @Test
    void jwtAuthenticationConverter_ShouldExtractClientAccessRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("resource_access", Map.of(
                        "dinet-pedidos",
                        Map.of("roles", List.of("carga", "consulta"))
                ))
                .build();

        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

        // FIX: el converter devuelve JwtAuthenticationToken, no Collection
        JwtAuthenticationToken auth = (JwtAuthenticationToken) converter.convert(jwt);

        assertNotNull(auth);
        assertEquals(2, auth.getAuthorities().size());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CARGA")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CONSULTA")));
    }

    // =======================
    //  JWT – Sin roles
    // =======================
    @Test
    void jwtAuthenticationConverter_ShouldReturnEmpty_WhenNoRoles() {
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                // FIX: agregar claim vacío para evitar IllegalArgument
                .claim("dummy", "x")
                .build();

        JwtAuthenticationToken auth = (JwtAuthenticationToken) converter.convert(jwt);

        assertEquals(0, auth.getAuthorities().size());
    }
}
