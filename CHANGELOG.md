# Changelog - calisat-ms-usuarios

## [1.1.4] - 2026-09-05

### Fixed
- Agregado fallback hacia claim `preferred_username` cuando `email` es nulo
- Corregido error 500 por ConstraintViolationException en email obligatorio
- Versión pom.xml actualizada a 1.1.4

## [1.1.3] - 2026-09-05

### Fixed
- Corregido error 500 al buscar llaves públicas de Azure AD
- Reemplazado `withJwkSetUri` por `withIssuerLocation` para autodescubrimiento correcto de JWK Set
- Versión pom.xml actualizada a 1.1.3

## [1.1.2] - 2026-09-05

### Fixed
- Corregido bug crítico: `setJwtValidator` sobrescribía validación de issuer con audience
- Implementado `DelegatingOAuth2TokenValidator` para combinar issuer + audience validators
- Hardcodeado todos los valores de configuración (modo académico)
- Versión pom.xml actualizada a 1.1.2

## [1.1.1] - 2026-09-05

### Fixed
- Corregido error 401 Unauthorized en endpoint POST /registro
- Agregado `AudienceValidator` para validar claim `aud` del JWT
- Configurado `issuer-uri` correctamente con URI completo de Azure AD
- Agregada variable `JWT_AUDIENCE` en docker-compose.yml y application.yaml

## [1.1.0] - 2026-09-04

### Added
- Endpoint POST /api/v1/usuarios/registro (201 Created o 200 OK si ya existía)
- Endpoint DELETE /api/v1/usuarios/perfil (baja lógica, 204 No Content)

### Changed
- Refactorizado GET /api/v1/usuarios/perfil: ya no hace JIT provisioning, retorna 404 si no existe
- Refactorizado PUT /api/v1/usuarios/perfil: retorna 404 si el perfil no existe
- UsuarioService: métodos separados registrarUsuario, buscarPorAzureSub, actualizarNombre, darDeBaja

## [1.0.2] - 2026-08-28

### Fixed
- Corregido error de tipado generico en UsuarioController (ResponseEntity<Map<String, Object>>)

## [1.0.1] - 2026-08-28

### Fixed
- Eliminadas credenciales por defecto en docker-compose.yml (CRIT-001)
- Eliminadas credenciales por defecto en application.yaml (CRIT-001)
- Cerrado puerto 5432 de PostgreSQL al host (CRIT-002)
- Todas las variables sensibles ahora son obligatorias (${VARIABLE} sin fallback)

## [1.0.0] - 2026-08-28

[1.1.4]: https://github.com/DavNat13/calisat-ms-usuarios/compare/v1.1.3...v1.1.4
[1.1.3]: https://github.com/DavNat13/calisat-ms-usuarios/compare/v1.1.2...v1.1.3
[1.1.2]: https://github.com/DavNat13/calisat-ms-usuarios/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/DavNat13/calisat-ms-usuarios/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/DavNat13/calisat-ms-usuarios/compare/v1.0.2...v1.1.0

### Added
- Microservicio calisat-ms-usuarios con Spring Boot 4.1.0 y Java 21
- Docker Compose con PostgreSQL 15 y app Spring Boot
- Modelos JPA: UsuarioProfile y DireccionEnvio
- Repositorios JPA para ambas entidades
- UsuarioService con logica de busqueda/creacion por azureSub
- UsuarioController con endpoint GET /api/v1/usuarios/perfil
- SecurityConfig para validacion JWT de Azure AD
- Configuracion via variables de entorno (cero archivos .env)
- Health check via Spring Actuator
