# Changelog - calisat-ms-usuarios

## [1.0.0] - 2026-08-28

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
