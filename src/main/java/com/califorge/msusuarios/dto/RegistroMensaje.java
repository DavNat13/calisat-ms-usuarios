package com.califorge.msusuarios.dto;

/**
 * Mensaje de registro publicado en RabbitMQ (exchange calisat.exchange).
 * El consumidor (ms-notificaciones) recibe la misma estructura JSON.
 */
public record RegistroMensaje(String identificador, String nombre, String email) {
}
