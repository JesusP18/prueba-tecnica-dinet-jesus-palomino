# Microservicio de Carga de Pedidos - Dinet

## Descripción

Microservicio construido con Spring Boot 3 para la carga masiva de pedidos desde archivos CSV, implementando arquitectura hexagonal, procesamiento por lotes e idempotencia. Diseñado para manejar volúmenes altos (optimizado hasta 100,000 registros por carga mediante batching y ajustes de JPA).

---

## Características principales

- Arquitectura hexagonal (domain / application / infrastructure)
- Carga masiva desde CSV con validaciones por fila
- Idempotencia mediante `Idempotency-Key` + hash SHA-256 del archivo
- Seguridad OAuth2 Resource Server (JWT / Keycloak)
- Procesamiento batch (tamaño configurable entre 500 y 1000)
- OpenAPI 3 + Swagger UI
- Logs estructurados JSON con `correlationId`

---

## Tecnologías

- Java 17
- Spring Boot 3.5.7
- PostgreSQL + Spring Data JPA
- Flyway para migraciones
- Spring Security (OAuth2 Resource Server)
- SpringDoc OpenAPI (Swagger UI)
- Apache Commons CSV
- Lombok

---

## Prerrequisitos

- Java 17+
- PostgreSQL 14+
- Maven 3.6+
- Docker / Docker Compose (opcional, recomendado para Keycloak)

---

## Configuración rápida

1. Clonar el repositorio

```
git clone https://github.com/JesusP18/prueba-tecnica-dinet-jesus-palomino
cd dinet-pedidos
```

2. Base de datos (local)

```
# Crear base de datos (ejemplo local)
createdb pedidos_db y keycloak_db
# O con Docker
docker run --name postgres-dinet -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=pedidos_db -p 5432:5432 -d postgres:14
```

3. Keycloak (opcional con docker-compose)

```
docker-compose up -d keycloak
```

4. Crear usuario/realm/client en Keycloak

Accede a `http://localhost:8081`  para configurar realm/cliente/usuarios
siguiendo la guía, se creó un archivo `Pasos_Para_Crear_Usuario_En_Keycloak.pdf` en la raíz del proyecto
donde podrás guiarte en la creación de un usuario y poder usar los endpoints protegidos.

---

## Configuración (resumen)

Fichero: `src/main/resources/application.yaml` (ajusta según entorno)

- Conexión a PostgreSQL
- Flyway habilitado
- Propiedad para batch: `app.batch.size` (valor entre 500 y 1000)

Ejemplo (parcial):

```yaml
app:
  batch:
    size: 500 # debe estar entre 500 y 1000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pedidos_db
    username: postgres # ajustar usuario según tu configuración
    password: 123456 # ajustar password según tu configuración
```

---

## Keycloak (resumen)

- URL Admin (ejemplo local): `http://localhost:8081`
- Realm: `dinet`
- Client: `dinet-pedidos` (tipo: public)
- Credenciales (ejemplo de despliegue local): admin/admin

### Keycloak con Docker Compose (solo Keycloak)

En este repositorio incluimos una configuración para levantar únicamente Keycloak usando Docker Compose. IMPORTANTE: las bases de datos PostgreSQL (`keycloak_db` y `pedidos_db`) deben crearse manualmente en tu instancia de PostgreSQL local; el `docker-compose` solo arranca Keycloak y conecta a la base de datos que indiques.

Ejemplo de servicio en `docker-compose.yml` (levanta Keycloak en modo dev):

```yaml
version: '3.8'
services:
  keycloak:
    image: quay.io/keycloak/keycloak:21.0.0
    container_name: dinet-keycloak
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_DB: postgres
      # Apunta al host donde corre Postgres (en Windows/Mac con Docker Desktop usar host.docker.internal)
      KC_DB_URL: jdbc:postgresql://host.docker.internal:5432/keycloak_db
      KC_DB_USERNAME: postgres # ajustar usuario según tu configuración
      KC_DB_PASSWORD: 123456 # ajustar password según tu configuración
      KC_HTTP_ENABLED: true
      KC_HOSTNAME_STRICT: false
      KC_HOSTNAME_STRICT_HTTPS: false
    ports:
      - "8081:8080"
    networks:
      - dinet-network
    command: start-dev

networks:
  dinet-network:
    driver: bridge
```

Notas importantes:
- `KC_DB_URL` en el ejemplo usa `host.docker.internal:5432` (funciona en Docker Desktop para Windows y macOS). En Linux podrías necesitar usar la IP del host o ejecutar Postgres en otro contenedor y enlazarlos.
- Este `docker-compose.yml` asume que la base de datos `keycloak_db` ya existe y es accesible desde el contenedor de Keycloak.
- La contraseña `KC_DB_PASSWORD` debe coincidir con la contraseña del usuario de PostgreSQL que administra `keycloak_db` (por ejemplo `postgres`).
- Levantar keycloak con el comando `docker-compose up -d` desde la terminal en la raíz del proyecto.

### Nota importante sobre Docker y las bases de datos

> Este `docker-compose.yml` solo levanta Keycloak; las bases de datos PostgreSQL deben crearse manualmente (por ejemplo `keycloak_db` y `pedidos_db`) en tu instancia de Postgres local. Asegúrate de que la contraseña del usuario PostgreSQL en tu máquina coincida con `KC_DB_PASSWORD` en `docker-compose.yml` y con `spring.datasource.password` en `src/main/resources/application.yaml`. Si no coinciden, modifica `KC_DB_PASSWORD` y `spring.datasource.password` antes de levantar Keycloak o la aplicación.

### Crear las bases de datos manualmente

A continuación tienes comandos de ejemplo para crear las BDs en una instalación local de PostgreSQL (ejecuta en el host donde corre Postgres):

```
# Conéctate como el usuario postgres (o el usuario administrador que tengas) y crea las BDs
psql -U postgres -c "CREATE DATABASE keycloak_db;"
psql -U postgres -c "CREATE DATABASE pedidos_db;"
```

Si tu Postgres requiere autenticación por contraseña, usa:

```
PGPASSWORD=tu_contraseña psql -U postgres -h localhost -c "CREATE DATABASE keycloak_db;"
PGPASSWORD=tu_contraseña psql -U postgres -h localhost -c "CREATE DATABASE pedidos_db;"
```

En Windows (cmd.exe) puedes usar:

```cmd
set PGPASSWORD=tu_contraseña
psql -U postgres -h localhost -c "CREATE DATABASE keycloak_db;"
psql -U postgres -h localhost -c "CREATE DATABASE pedidos_db;"
```

### Ajustar contraseñas y configuración

- Asegúrate de que la contraseña del usuario PostgreSQL (por ejemplo `postgres`) sea la misma que pones en `KC_DB_PASSWORD` del `docker-compose.yml` y en `spring.datasource.password` dentro de `src/main/resources/application.yaml` (para la app `pedidos`).
- Si tu contraseña es diferente en el equipo donde despliegas, modifica `docker-compose.yml` antes de levantar Keycloak y modifica `application.yaml` para que la aplicación se conecte a `pedidos_db` con las credenciales correctas.

### Levantar Keycloak en el cmd a la altura del proyecto

```
# En la carpeta que contiene docker-compose.yml
docker-compose up -d keycloak
```

Verifica logs:

```
docker logs -f dinet-keycloak
```

---

## Autenticación

Tres maneras principales para obtener/usar el JWT:

1. Desde Swagger UI: haz clic en `Authorize` y pega `Bearer <token>`.
2. Obtener token manual (curl):

```
curl -X POST http://localhost:8081/realms/dinet/protocol/openid-connect/token
  -H "Content-Type: application/x-www-form-urlencoded"
  -d "username=user&password=admin&grant_type=password&client_id=dinet-pedidos"
```

3. Usar Postman con la colección provista.

---

## API principal

Endpoint: `POST /pedidos/cargar`

Headers obligatorios:
- `Authorization`: Bearer <token JWT>
- `Idempotency-Key`: <clave-única-por-carga>

Body: form-data con campo `file` conteniendo el CSV.

En la raíz del proyecto hay un archivo de ejemplo `test.csv` para pruebas.

Ejemplo (curl):

```
curl -X POST http://localhost:8080/pedidos/cargar \
  -H "Authorization: Bearer <token>" \
  -H "Idempotency-Key: carga-123" \
  -F "file=@pedidos.csv"
```

---

## Formato del CSV

- Archivo UTF-8
- Delimitador: coma (,)
- Cabecera requerida: `numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion`
- Formato `fechaEntrega`: `YYYY-MM-DD` (LocalDate)

Ejemplo:

```
numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion
P001,CLI-123,2025-08-10,PENDIENTE,ZONA1,true
P002,CLI-999,2025-08-12,ENTREGADO,ZONA5,false
```

---

## Validaciones implementadas (reglas de negocio)

- numeroPedido: alfanumérico y único en BD
- clienteId: debe existir y estar activo en la tabla `clientes`
- fechaEntrega: formato `YYYY-MM-DD`, no puede ser pasada respecto a la fecha actual en la zona `America/Lima`
- estado: uno de `PENDIENTE`, `CONFIRMADO`, `ENTREGADO`
- zonaEntrega: debe existir en la tabla `zonas`
- requiereRefrigeracion: `true`/`false`; si es `true`, la zona debe soportar refrigeración (`zonas.soporte_refrigeracion = true`)

Comportamiento al validar el archivo:
- Se parsea y valida línea por línea.
- Se detectan y reportan errores por línea (numero de línea y motivo).
- Si hay cualquier error en el archivo, NO se persisten registros y se devuelve el resumen con errores.
- Se detectan duplicados dentro del mismo archivo (DUPLICADO_EN_ARCHIVO) y duplicados contra BD (DUPLICADO).

---

## Respuesta de la API (modelo de error/resultado)

Respuesta ejemplo (JSON):

```json
{
  "totalProcesados": 150,
  "guardados": 145,
  "conError": 5,
  "errores": [
    { "numeroLinea": 57, "motivo": "Cliente no existe: CLI-999", "errorCode": "CLIENTE_NO_EXISTE" }
  ]
}
```

Modelo de error estandarizado incluye: `code`, `message`, `details[]`, `correlationId`.

---

## Idempotencia

- Header obligatorio: `Idempotency-Key` en `POST /pedidos/cargar`.
- Se calcula hash SHA-256 del archivo recibido.
- Se registra en la tabla `cargas_idempotencia` la tupla `(idempotency_key, archivo_hash)` para evitar reprocesos.
- Si una misma `Idempotency-Key` y hash ya existen, se devuelve respuesta indicando carga duplicada sin efectos secundarios.

---

## Estrategia de batch y performance

- Tamaño de lote configurable por `app.batch.size` y limitado automáticamente entre 500 y 1000.
- Se hacen inserciones por lotes con JPA batching (propiedades de Hibernate ajustadas en `application.yaml`).
- Se precargan catálogos (clientes/zonas) cuando aplica para reducir I/O por fila.

---

## Arquitectura y estructura del proyecto

Estructura (resumen):

```
src/
  ├── domain/          # Lógica de negocio pura (entidades, excepciones, servicios)
  ├── application/     # Casos de uso y orquestación (services)
  └── infrastructure/  # Adaptadores: REST controllers, JPA entities/repositories, config
```

Patrones: Ports & Adapters, DDD-light (entidades y servicios de dominio), Repository pattern.

---

## OpenAPI / Swagger

- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html` (o `/swagger-ui.html` según la versión)

Nota: Swagger UI ofrece esquema de seguridad Bearer para facilitar pruebas (botón Authorize).

---

## Testing

- Incluye pruebas unitarias (servicios de dominio). Ejecutar:

```
mvn test
```

Objetivo de cobertura: >= 80% en servicios de dominio.
Se agregó Jacoco para reportes de cobertura. se puede generar reporte con:

```
mvn clean verify
```

El reporte se genera en `target/jacoco-report/index.html`.

---

## Observabilidad y logs

- Logs estructurados en formato JSON e incluyen `correlationId` para trazar peticiones.
- Métricas disponibles: tiempos de procesamiento, tasa de éxito/errores, uso de recursos.

Ejemplo de log:

```json
{
  "timestamp": "2024-01-15 14:30:45.123",
  "level": "INFO",
  "correlationId": "abc-123",
  "logger": "com.dinet.pedidos",
  "message": "Procesando archivo CSV"
}
```

---

## Límites conocidos

- Tamaño máximo archivo: configurable (por defecto 10MB)
- Registros por carga: optimizado hasta 100,000 (ajustar recursos)
- Tiempo máximo estimado de procesamiento: configurable (ej. 5 minutos)

---

## Flujo de procesamiento

1. Validación de headers (Authorization + Idempotency-Key)
2. Cálculo del hash SHA-256 del archivo
3. Verificación de idempotencia
4. Parseo y validación línea por línea (reportar errores)
5. Si no hay errores: persistir por lotes
6. Registrar resultado y la carga idempotente

---

## Consideraciones de seguridad

- Todas las rutas protegidas por OAuth2 (excepto recursos de documentación y `v3/api-docs`).
- Tokens JWT validados contra Keycloak.
- No se registran datos sensibles en logs.
- CORS: durante desarrollo se permite orígenes para facilitar Swagger UI; en producción restringir.
