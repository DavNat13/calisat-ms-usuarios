package com.califorge.msusuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario_perfil")
public class UsuarioProfile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @NotBlank(message = "azureSub es obligatorio")
    @Column(name = "azure_sub", unique = true, nullable = false, length = 128)
    private String azureSub;

    @NotBlank(message = "email es obligatorio")
    @Email(message = "email debe ser valido")
    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "nombre_completo", length = 255)
    private String nombreCompleto;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "usuarioProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<DireccionEnvio> direcciones = new java.util.ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    // Constructores
    public UsuarioProfile() {}

    public UsuarioProfile(String azureSub, String email, String nombreCompleto) {
        this.azureSub = azureSub;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAzureSub() { return azureSub; }
    public void setAzureSub(String azureSub) { this.azureSub = azureSub; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public java.util.List<DireccionEnvio> getDirecciones() { return direcciones; }
    public void setDirecciones(java.util.List<DireccionEnvio> direcciones) { this.direcciones = direcciones; }
}
