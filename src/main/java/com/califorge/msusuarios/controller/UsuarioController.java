package com.califorge.msusuarios.controller;

import com.califorge.msusuarios.dto.RegistroMensaje;
import com.califorge.msusuarios.model.UsuarioProfile;
import com.califorge.msusuarios.service.UsuarioService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RabbitTemplate rabbitTemplate;

    public UsuarioController(UsuarioService usuarioService, RabbitTemplate rabbitTemplate) {
        this.usuarioService = usuarioService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * POST /api/v1/usuarios/registro
     * Registra el usuario desde el JWT. 201 si es nuevo, 200 si ya existía.
     * Si es nuevo, publica un evento de registro en RabbitMQ (calisat.exchange).
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(
            @AuthenticationPrincipal Jwt jwt) {

        String azureSub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            email = jwt.getClaimAsString("preferred_username");
        }
        String nombre = jwt.getClaimAsString("name");
        String rol = extraerRol(jwt);

        boolean existia = usuarioService.buscarPorAzureSub(azureSub).isPresent();
        UsuarioProfile perfil = usuarioService.registrarUsuario(azureSub, email, nombre, rol);

        if (!existia) {
            rabbitTemplate.convertAndSend(
                    new RegistroMensaje(
                            perfil.getId().toString(),
                            perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                            perfil.getEmail()));
        }

        Map<String, Object> response = Map.of(
                "id", perfil.getId().toString(),
                "azureSub", perfil.getAzureSub(),
                "email", perfil.getEmail(),
                "nombreCompleto", perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                "fechaRegistro", perfil.getFechaRegistro().toString(),
                "activo", perfil.getActivo(),
                "rol", perfil.getRol() != null ? perfil.getRol() : ""
        );

        return existia
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(201).body(response);
    }

    /**
     * Extrae el primer rol del claim "roles" del JWT de Azure Entra ID.
     */
    private String extraerRol(Jwt jwt) {
        Object roles = jwt.getClaim("roles");
        if (roles instanceof java.util.Collection<?> coleccion) {
            return coleccion.stream()
                    .filter(obj -> obj != null && !obj.toString().isBlank())
                    .findFirst()
                    .map(Object::toString)
                    .orElse(null);
        }
        if (roles instanceof String texto && !texto.isBlank()) {
            return texto;
        }
        return null;
    }

    /**
     * GET /api/v1/usuarios/perfil
     * Busca el perfil por sub del JWT. 404 si no existe.
     */
    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(
            @AuthenticationPrincipal Jwt jwt) {

        String azureSub = jwt.getSubject();

        return usuarioService.buscarPorAzureSub(azureSub)
                .map(perfil -> ResponseEntity.ok(Map.<String, Object>of(
                        "id", perfil.getId().toString(),
                        "azureSub", perfil.getAzureSub(),
                        "email", perfil.getEmail(),
                        "nombreCompleto", perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                        "fechaRegistro", perfil.getFechaRegistro().toString(),
                        "activo", perfil.getActivo(),
                        "rol", perfil.getRol() != null ? perfil.getRol() : ""
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/usuarios/perfil
     * Actualiza nombre completo. 404 si no existe.
     */
    @PutMapping("/perfil")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> body) {

        String azureSub = jwt.getSubject();
        String nuevoNombre = body.get("nombreCompleto");

        return usuarioService.actualizarNombre(azureSub, nuevoNombre)
                .map(perfil -> ResponseEntity.ok(Map.<String, Object>of(
                        "id", perfil.getId().toString(),
                        "azureSub", perfil.getAzureSub(),
                        "email", perfil.getEmail(),
                        "nombreCompleto", perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                        "mensaje", "Perfil actualizado correctamente"
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/usuarios/perfil
     * Baja lógica (activo=false). 204 si ok, 404 si no existe.
     */
    @DeleteMapping("/perfil")
    public ResponseEntity<Void> eliminarPerfil(
            @AuthenticationPrincipal Jwt jwt) {

        String azureSub = jwt.getSubject();

        return usuarioService.darDeBaja(azureSub)
                .map(perfil -> ResponseEntity.noContent().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }
}
