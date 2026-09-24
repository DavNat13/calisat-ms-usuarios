package com.califorge.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionRequest(
        @Size(max = 100, message = "alias no puede superar 100 caracteres")
        String alias,

        @NotBlank(message = "calle es obligatoria")
        @Size(max = 255, message = "calle no puede superar 255 caracteres")
        String calle,

        @NotBlank(message = "ciudad es obligatoria")
        @Size(max = 100, message = "ciudad no puede superar 100 caracteres")
        String ciudad,

        @Size(max = 100, message = "estado no puede superar 100 caracteres")
        String estado,

        @Size(max = 20, message = "codigoPostal no puede superar 20 caracteres")
        String codigoPostal,

        @NotBlank(message = "pais es obligatorio")
        @Size(max = 100, message = "pais no puede superar 100 caracteres")
        String pais,

        boolean esPredeterminada) {
}
