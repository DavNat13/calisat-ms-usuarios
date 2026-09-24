package com.califorge.msusuarios.controller;

import com.califorge.msusuarios.dto.DireccionRequest;
import com.califorge.msusuarios.dto.DireccionResponse;
import com.califorge.msusuarios.service.DireccionEnvioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios/perfil/direcciones")
public class DireccionEnvioController {

    private final DireccionEnvioService direccionEnvioService;

    public DireccionEnvioController(DireccionEnvioService direccionEnvioService) {
        this.direccionEnvioService = direccionEnvioService;
    }

    /**
     * GET /api/v1/usuarios/perfil/direcciones
     * Lista las direcciones del usuario autenticado (scope por sub del JWT). 404 si no existe el perfil.
     */
    @GetMapping
    public ResponseEntity<List<DireccionResponse>> listar(
            @AuthenticationPrincipal Jwt jwt) {

        return direccionEnvioService.listar(jwt.getSubject())
                .map(direcciones -> ResponseEntity.ok(
                        direcciones.stream().map(DireccionResponse::desde).toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/usuarios/perfil/direcciones
     * Crea una dirección para el usuario autenticado. 201 + Location .../{id}; 404 si no existe el perfil.
     */
    @PostMapping
    public ResponseEntity<DireccionResponse> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DireccionRequest request) {

        return direccionEnvioService.crear(jwt.getSubject(), request)
                .map(direccion -> {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(direccion.getId())
                            .toUri();
                    return ResponseEntity.created(location).body(DireccionResponse.desde(direccion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/usuarios/perfil/direcciones/{id}
     * Actualiza una dirección del usuario autenticado. 200 si ok; 404 si no existe o no pertenece al perfil.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DireccionResponse> actualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody DireccionRequest request) {

        return direccionEnvioService.actualizar(jwt.getSubject(), id, request)
                .map(DireccionResponse::desde)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/usuarios/perfil/direcciones/{id}
     * Elimina una dirección del usuario autenticado. 204 si ok; 404 si no existe o no pertenece al perfil.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {

        return direccionEnvioService.eliminar(jwt.getSubject(), id)
                .map(direccion -> ResponseEntity.noContent().<Void>build())
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/v1/usuarios/perfil/direcciones/{id}/predeterminada
     * Marca una dirección como predeterminada. 200 si ok; 404 si no existe o no pertenece al perfil.
     */
    @PutMapping("/{id}/predeterminada")
    public ResponseEntity<DireccionResponse> marcarPredeterminada(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {

        return direccionEnvioService.marcarPredeterminada(jwt.getSubject(), id)
                .map(DireccionResponse::desde)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
