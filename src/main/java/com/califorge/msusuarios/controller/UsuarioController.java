package com.califorge.msusuarios.controller;

import com.califorge.msusuarios.model.UsuarioProfile;
import com.califorge.msusuarios.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/v1/usuarios/perfil
     * Extrae el claim "sub" del JWT de Azure AD y retorna/crea el perfil.
     */
    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> obtenerPerfil(@AuthenticationPrincipal Jwt jwt) {
        String azureSub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String nombre = jwt.getClaimAsString("name");

        UsuarioProfile perfil = usuarioService.buscarOrCreate(azureSub, email, nombre);

        return ResponseEntity.ok(Map.of(
                "id", perfil.getId().toString(),
                "azureSub", perfil.getAzureSub(),
                "email", perfil.getEmail(),
                "nombreCompleto", perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                "fechaRegistro", perfil.getFechaRegistro().toString(),
                "activo", perfil.getActivo()
        ));
    }

    /**
     * PUT /api/v1/usuarios/perfil
     * Actualiza el nombre completo del perfil.
     */
    @PutMapping("/perfil")
    public ResponseEntity<Map<String, Object>> actualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> body) {

        String azureSub = jwt.getSubject();
        String nuevoNombre = body.get("nombreCompleto");

        return usuarioService.actualizarNombre(azureSub, nuevoNombre)
                .map(perfil -> ResponseEntity.ok(Map.of(
                        "id", perfil.getId().toString(),
                        "azureSub", perfil.getAzureSub(),
                        "email", perfil.getEmail(),
                        "nombreCompleto", perfil.getNombreCompleto() != null ? perfil.getNombreCompleto() : "",
                        "mensaje", "Perfil actualizado correctamente"
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
