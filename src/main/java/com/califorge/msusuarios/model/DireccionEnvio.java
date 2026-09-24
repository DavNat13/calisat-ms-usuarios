package com.califorge.msusuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "direccion_envio")
public class DireccionEnvio {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioProfile usuarioProfile;

    @Column(length = 100)
    private String alias;

    @NotBlank(message = "calle es obligatoria")
    @Column(nullable = false, length = 255)
    private String calle;

    @NotBlank(message = "ciudad es obligatoria")
    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 100)
    private String estado;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @NotBlank(message = "pais es obligatorio")
    @Column(nullable = false, length = 100)
    private String pais;

    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada = false;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    // Constructores
    public DireccionEnvio() {}

    public DireccionEnvio(UsuarioProfile usuarioProfile, String alias, String calle, String ciudad, String estado, String codigoPostal, String pais) {
        this.usuarioProfile = usuarioProfile;
        this.alias = alias;
        this.calle = calle;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.pais = pais;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UsuarioProfile getUsuarioProfile() { return usuarioProfile; }
    public void setUsuarioProfile(UsuarioProfile usuarioProfile) { this.usuarioProfile = usuarioProfile; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Boolean getEsPredeterminada() { return esPredeterminada; }
    public void setEsPredeterminada(Boolean esPredeterminada) { this.esPredeterminada = esPredeterminada; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
