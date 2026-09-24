# calisat-ms-usuarios

> Microservicio Spring Boot de perfiles y direcciones de envío: registro desde el JWT de Entra ID, gestión del perfil propio y CRUD de direcciones.

![Versión](https://img.shields.io/badge/version-1.2.0-2563EB)
![Java](https://img.shields.io/badge/Java-21-F89820?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)
![Estado](https://img.shields.io/badge/estado-modo%20acad%C3%A9mico-FACC15)

**Versión actual: `1.2.0`** (definida en `pom.xml` · historial en [`CHANGELOG.md`](CHANGELOG.md))

---

## 📑 Índice

- [📋 Descripción general](#-descripción-general)
- [✨ Características principales](#-características-principales)
- [🏗️ Arquitectura](#-arquitectura)
- [🚀 Requisitos](#-requisitos)
- [⚙️ Configuración](#-configuración)
- [▶️ Ejecución local](#-ejecución-local)
- [📡 Endpoints principales](#-endpoints-principales)
- [🗃️ Modelo de datos](#-modelo-de-datos)
- [🔒 Seguridad](#-seguridad)
- [🧪 Tests](#-tests)
- [📦 Despliegue](#-despliegue)
- [🔗 Microservicios relacionados](#-microservicios-relacionados)
- [📄 Licencia y modo académico](#-licencia-y-modo-académico)

---

## 📋 Descripción general

**calisat-ms-usuarios** administra el **perfil de usuario** y sus **direcciones de envío** en la plataforma Calisat. El usuario no se crea con un formulario: se **registra a partir del JWT** de Microsoft Entra ID (`sub`, `email`/`preferred_username`, `name`), de modo que el frontend solo invoca `POST /api/v1/usuarios/registro` tras el inicio de sesión (lo hace automáticamente `useUserSync`).

Toda la API está **scopeada por el `sub` del JWT**: cada usuario solo ve y modifica su propio perfil y sus propias direcciones. No hay roles ni RBAC (*modo académico*).

## ✨ Características principales

- 👤 **Registro automático desde el JWT**: `201` si es nuevo, `200` si ya existía (idempotente).
- 🧾 **Perfil propio**: consulta, actualización de nombre completo y baja lógica (`activo=false`).
- 📬 **CRUD de direcciones de envío**: alta, listado, edición, borrado y marcado de **dirección predeterminada**.
- 🔒 **Aislamiento por usuario**: todas las operaciones se resuelven con el `sub` del token; `404` si el recurso no pertenece al perfil.
- 🧩 **`GlobalExceptionHandler`**: manejo global de validación, JSON malformado, 404 e integridad.
- 🩺 **Actuator** (`/actuator/**` público) para health checks.
- 🔑 **Validación de audience** con `AudienceValidator` + JJWT (`jjwt` 0.11.5) disponible en el classpath.
- 🐳 **Docker multi-stage** con usuario no root y health check.

## 🏗️ Arquitectura

```mermaid
flowchart LR
    F[calisat-frontend] -->|POST /registro · JWT| U[calisat-ms-usuarios<br/>:8081]
    F -->|GET/PUT/DELETE /perfil · JWT| U
    F -->|CRUD direcciones · JWT| U
    U --> PG[(PostgreSQL<br/>calisat_usuarios)]
    U --> AC[/actuator]
```

### Estructura de paquetes

```
com.califorge.msusuarios
├── config/        # SecurityConfig, AudienceValidator, CORS
├── controller/    # UsuarioController, DireccionEnvioController
├── dto/           # DireccionRequest, DireccionResponse
├── exception/     # GlobalExceptionHandler
├── model/         # UsuarioProfile, DireccionEnvio (JPA)
├── repository/    # UsuarioProfileRepository, DireccionEnvioRepository
└── service/       # UsuarioService, DireccionEnvioService
```

## 🚀 Requisitos

| Requisito | Versión mínima |
|-----------|----------------|
| JDK | **21+** |
| Maven | 3.6.3+ (o wrapper `./mvnw`) |
| Docker + Docker Compose | 24+ |
| Cuenta Entra ID | Para emitir JWT válidos (modo académico) |

## ⚙️ Configuración

Valores de `src/main/resources/application.yaml` y `docker-compose.yml`:

| Parámetro | Valor |
|-----------|-------|
| **Puerto del servicio** | **`8081`** (Docker Compose publica `8081:8080`; la app en contenedor escucha en `8080`) |
| Base de datos | PostgreSQL · `calisat_usuarios` |
| Host de BD (local) | `postgres:5432` (en Compose, servicio `postgres`) |
| Usuario / contraseña BD | `postgres` / `postgres` *(solo académico)* |
| `ddl-auto` | `update` |
| JWT *issuer* | `https://login.microsoftonline.com/e5372bf0-c5e3-4286-887c-79069f209c1f/v2.0` |
| JWT *audience* | `d221f0d2-1a7c-4872-ad6c-367a1f0717ec` |
| Rutas públicas | `/api/v1/public/**`, `/actuator/**` |
| Actuator | `health`, `info` |

> ⚠️ **Modo académico**: issuer, audience y credenciales de BD están **hardcodeados** a propósito; en producción deben externalizarse.

## ▶️ Ejecución local

### 1. Base de datos

```bash
docker compose up -d postgres
```

### 2. Aplicación

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

> 💡 El `application.yaml` declara `server.port: 8080`. Para exponerlo en el puerto del ecosistema (**8081**) fuera de Docker: `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`.

### 3. Docker Compose

```bash
docker compose up --build
```

Servicio disponible en `http://localhost:8081`.

## 📡 Endpoints principales

Base: `http://localhost:8081/api/v1/usuarios`

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| `POST` | `/api/v1/usuarios/registro` | Registra el perfil desde el JWT (`201` nuevo · `200` existente) | JWT |
| `GET` | `/api/v1/usuarios/perfil` | Perfil del usuario autenticado (404 si no existe) | JWT |
| `PUT` | `/api/v1/usuarios/perfil` | Actualiza `nombreCompleto` | JWT |
| `DELETE` | `/api/v1/usuarios/perfil` | Baja lógica del perfil (`204`; `404` si no existe) | JWT |
| `GET` | `/api/v1/usuarios/perfil/direcciones` | Lista las direcciones del usuario | JWT |
| `POST` | `/api/v1/usuarios/perfil/direcciones` | Crea dirección (`201` + `Location`) | JWT |
| `PUT` | `/api/v1/usuarios/perfil/direcciones/{id}` | Actualiza dirección (404 si no pertenece) | JWT |
| `DELETE` | `/api/v1/usuarios/perfil/direcciones/{id}` | Elimina dirección (`204`) | JWT |
| `PUT` | `/api/v1/usuarios/perfil/direcciones/{id}/predeterminada` | Marca dirección predeterminada | JWT |

**Total: 9 endpoints** (2 controladores)

### Ejemplo

```bash
# Registro tras login (lo hace el frontend con useUserSync)
curl -X POST http://localhost:8081/api/v1/usuarios/registro \
  -H "Authorization: Bearer $TOKEN"

# Crear dirección de envío
curl -X POST http://localhost:8081/api/v1/usuarios/perfil/direcciones \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"calle":"Av. Siempreviva 742","ciudad":"Springfield","pais":"US","codigoPostal":"97402"}'
```

## 🗃️ Modelo de datos

### Entidad `UsuarioProfile` (tabla `usuario_perfil`)

| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | `UUID` | PK, generador UUID |
| `azure_sub` | `String(128)` | **Único**, `NOT NULL` — `sub` del JWT |
| `email` | `String(255)` | `NOT NULL`, `@Email` |
| `nombre_completo` | `String(255)` | Opcional |
| `fecha_registro` | `LocalDateTime` | `@PrePersist` |
| `activo` | `Boolean` | Default `true` (baja lógica) |
| `direcciones` | `1..N` | `cascade=ALL`, `orphanRemoval` |

### Entidad `DireccionEnvio` (tabla `direccion_envio`)

| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | `UUID` | PK |
| `usuario_id` | FK → `usuario_perfil` | `NOT NULL` (`@ManyToOne LAZY`) |
| `alias` | `String(100)` | Opcional |
| `calle` | `String(255)` | `NOT NULL` |
| `ciudad` | `String(100)` | `NOT NULL` |
| `estado` | `String(100)` | Opcional (provincia/estado) |
| `codigo_postal` | `String(20)` | Opcional |
| `pais` | `String(100)` | `NOT NULL` |
| `es_predeterminada` | `Boolean` | Default `false` |
| `fecha_creacion` | `LocalDateTime` | `@PrePersist` |

## 🔒 Seguridad

- **JWT (OAuth2 Resource Server)** de **Microsoft Entra ID**: validación de *issuer* + `AudienceValidator` (`DelegatingOAuth2TokenValidator`).
- **Sin RBAC**: un único nivel autenticado; **usuario genérico** — cada petición se scopea con `jwt.getSubject()` (*modo académico*).
- **Rutas públicas**: `/api/v1/public/**` y `/actuator/**`; el resto exige token.
- **CSRF deshabilitado** (API stateless con Bearer) · **CORS** restringido al origen del despliegue.
- El token **nunca se devuelve** en las respuestas del API (regresión corregida en v1.1.6).

## 🧪 Tests

Este repositorio **no incluye suite de tests** en `src/test` en la versión actual.

```bash
# Verificar compilación
./mvnw compile
```

Se recomienda añadir tests de `UsuarioService` y controladores siguiendo el patrón de `calisat-ms-catalogo`.

## 📦 Despliegue

### Docker

```bash
docker build -t calisat-ms-usuarios:1.2.0 .
docker run -p 8081:8080 --name calisat-ms-usuarios calisat-ms-usuarios:1.2.0
```

**Dockerfile multi-stage:**

1. `maven` (Temurin 21) → `mvn clean package`.
2. `eclipse-temurin:21-jre-alpine` → JAR con usuario no root, `MaxRAMPercentage=75`, `HEALTHCHECK` en `/actuator/health`.

### Docker Compose

```bash
docker compose up --build
```

Levanta PostgreSQL 15 (`calisat_usuarios`, volumen `calisat_usuarios_data`, red `calisat-usuarios-net`) y la app publicada en **8081**.

## 🔗 Microservicios relacionados

| Repositorio | Relación |
|-------------|----------|
| [calisat-frontend](https://github.com/DavNat13/calisat-frontend) | Registra el perfil al hacer login (`POST /api/v1/usuarios/registro` vía `useUserSync`) y muestra el perfil en `/perfil` |
| [calisat-ms-catalogo](https://github.com/DavNat13/calisat-ms-catalogo) | Catálogo de productos (puerto 8082) |
| [calisat-ms-carrito](https://github.com/DavNat13/calisat-ms-carrito) | Carrito por usuario autenticado (puerto 8084) |
| [calisat-ms-orden](https://github.com/DavNat13/calisat-ms-orden) | Órdenes del usuario (puerto 8085) |
| [calisat-ms-inventario](https://github.com/DavNat13/calisat-ms-inventario) | Stock y reservas (puerto 8083) |
| [calisat-ms-envios](https://github.com/DavNat13/calisat-ms-envios) | Envíos y seguimiento (puerto 8086) |
| [calisat-ms-notificaciones](https://github.com/DavNat13/calisat-ms-notificaciones) | Notificaciones (puerto 8087) |

## 📄 Licencia y modo académico

Proyecto desarrollado en **modo académico**; sin licencia open source formal. Issuer, audience y credenciales de BD están *hardcodeados* con fines educativos; no hay RBAC (usuario genérico autenticado).

- **Versión actual**: `1.2.0`
- **Historial de cambios**: [`CHANGELOG.md`](CHANGELOG.md)
