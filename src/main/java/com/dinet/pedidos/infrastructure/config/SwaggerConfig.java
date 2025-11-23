package com.dinet.pedidos.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(new Info()
                        .title("Dinet Pedidos API")
                        .version("1.0")
                        .description("""
                        API para gestión de pedidos.
                        
                        ### Autenticación
                        Para obtener un token de acceso:
                        1. Realiza una petición POST a: `http://localhost:8081/realms/dinet/protocol/openid-connect/token`
                        2. Usa `application/x-www-form-urlencoded` como tipo de contenido.
                        3. Con los parámetros:
                           - `username`: user
                           - `password`: admin
                           - `grant_type`: password
                           - `client_id`: dinet-pedidos
                        4. Puede pegar esto dentro de postman:
                                `curl -X POST http://localhost:8081/realms/dinet/protocol/openid-connect/token
                                  -H "Content-Type: application/x-www-form-urlencoded"
                                  -d "username=user&password=admin&grant_type=password&client_id=dinet-pedidos" `
                        5. Recibirás un JSON con el token en el campo `access_token`.
                        Luego usa el token en el header: `Authorization: Bearer <token>`
                        """));
    }
}
