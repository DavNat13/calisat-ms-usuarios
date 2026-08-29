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
     * Busca un perfil por azureSub. Si no existe, lo crea automaticamente
     * con los datos basicos del token JWT de Azure AD.
     */
    public UsuarioProfile buscarOrCreate(String azureSub, String email, String nombreCompleto) {
        Optional<UsuarioProfile> existente = usuarioProfileRepository.findByAzureSub(azureSub);

        if (existente.isPresent()) {
            return existente.get();
        }

        UsuarioProfile nuevo = new UsuarioProfile(azureSub, email, nombreCompleto);
        return usuarioProfileRepository.save(nuevo);
    }

    /**
     * Busca un perfil por azureSub.
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
}
