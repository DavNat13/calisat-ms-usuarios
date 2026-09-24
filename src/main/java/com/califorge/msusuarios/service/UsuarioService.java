package com.califorge.msusuarios.service;

import com.califorge.msusuarios.model.UsuarioProfile;
import com.califorge.msusuarios.repository.UsuarioProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioProfileRepository usuarioProfileRepository;

    public UsuarioService(UsuarioProfileRepository usuarioProfileRepository) {
        this.usuarioProfileRepository = usuarioProfileRepository;
    }

    /**
     * Registra un usuario. Si ya existe, retorna el perfil existente
     * (sincronizando el rol). Si no existe, lo crea con los datos del JWT.
     */
    public UsuarioProfile registrarUsuario(String azureSub, String email, String nombreCompleto, String rol) {
        Optional<UsuarioProfile> existente = usuarioProfileRepository.findByAzureSub(azureSub);
        if (existente.isPresent()) {
            UsuarioProfile perfil = existente.get();
            if (rol != null && !rol.equals(perfil.getRol())) {
                perfil.setRol(rol);
                return usuarioProfileRepository.save(perfil);
            }
            return perfil;
        }
        UsuarioProfile nuevo = new UsuarioProfile(azureSub, email, nombreCompleto);
        nuevo.setRol(rol);
        return usuarioProfileRepository.save(nuevo);
    }

    /**
     * Busca un perfil por azureSub. Retorna Optional vacío si no existe.
     */
    @Transactional(readOnly = true)
    public Optional<UsuarioProfile> buscarPorAzureSub(String azureSub) {
        return usuarioProfileRepository.findByAzureSub(azureSub);
    }

    /**
     * Actualiza el nombre completo del perfil.
     */
    public Optional<UsuarioProfile> actualizarNombre(String azureSub, String nuevoNombre) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .map(usuario -> {
                    usuario.setNombreCompleto(nuevoNombre);
                    return usuarioProfileRepository.save(usuario);
                });
    }

    /**
     * Baja lógica: cambia el estado activo a false.
     */
    public Optional<UsuarioProfile> darDeBaja(String azureSub) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .map(usuario -> {
                    usuario.setActivo(false);
                    return usuarioProfileRepository.save(usuario);
                });
    }
}
