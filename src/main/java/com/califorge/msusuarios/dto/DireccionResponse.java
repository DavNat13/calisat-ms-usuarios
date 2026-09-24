package com.califorge.msusuarios.dto;

import com.califorge.msusuarios.model.DireccionEnvio;

import java.time.LocalDateTime;
import java.util.UUID;

public record DireccionResponse(
        UUID id,
        String alias,
        String calle,
        String ciudad,
        String estado,
        String codigoPostal,
        String pais,
        boolean esPredeterminada,
        LocalDateTime fechaCreacion) {

    public static DireccionResponse desde(DireccionEnvio d) {
        return new DireccionResponse(
                d.getId(),
                d.getAlias(),
                d.getCalle(),
                d.getCiudad(),
                d.getEstado(),
                d.getCodigoPostal(),
                d.getPais(),
                Boolean.TRUE.equals(d.getEsPredeterminada()),
                d.getFechaCreacion());
    }
}
