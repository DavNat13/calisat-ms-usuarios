package com.califorge.msusuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.GenericGenerator;

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

    @NotBlank(message = "pais es obligatorio")
    @Column(nullable = false, length = 100)
    private String pais;

    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada = false;

    // Constructores
    public DireccionEnvio() {}

    public DireccionEnvio(UsuarioProfile usuarioProfile, String alias, String calle, String ciudad, String pais) {
        this.usuarioProfile = usuarioProfile;
        this.alias = alias;
        this.calle = calle;
        this.ciudad = ciudad;
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

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Boolean getEsPredeterminada() { return esPredeterminada; }
    public void setEsPredeterminada(Boolean esPredeterminada) { this.esPredeterminada = esPredeterminada; }
}
