# Microservicio de Carga de Pedidos

## Requisitos

- Java 17
- PostgreSQL
- Maven

## Configuración

1. Clonar el repositorio.
2. Configurar la base de datos en `application.yaml`.
3. Ejecutar la aplicación con `mvn spring-boot:run`.

## Uso

### Cargar pedidos

Endpoint: `POST /pedidos/cargar`

Headers:
- `Idempotency-Key`: [clave única]
- `Authorization`: Bearer [token JWT]

Body: form-data con el archivo CSV en el campo `file`.

### Documentación de API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs

## Estructura del CSV

El archivo CSV debe tener las siguientes columnas:

- numeroPedido
- clienteId
- fechaEntrega (formato: YYYY-MM-DD)
- estado (PENDIENTE, CONFIRMADO, ENTREGADO)
- zonaEntrega
- requiereRefrigeracion (true o false)

## Estrategia de Batch

- Tamaño de lote configurable (por defecto 500)
- Se validan y guardan los pedidos en lotes
- Se precargan los catálogos (clientes y zonas) en memoria para evitar consultas individuales

## Idempotencia

Se utiliza el header `Idempotency-Key` y el hash SHA-256 del archivo para evitar procesamientos duplicados.

## Seguridad

OAuth2 Resource Server con JWT. Todas las rutas requieren autenticación excepto la documentación de OpenAPI.