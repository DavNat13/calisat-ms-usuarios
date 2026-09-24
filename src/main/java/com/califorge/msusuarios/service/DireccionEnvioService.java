package com.califorge.msusuarios.service;

import com.califorge.msusuarios.dto.DireccionRequest;
import com.califorge.msusuarios.model.DireccionEnvio;
import com.califorge.msusuarios.model.UsuarioProfile;
import com.califorge.msusuarios.repository.DireccionEnvioRepository;
import com.califorge.msusuarios.repository.UsuarioProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DireccionEnvioService {

    private final DireccionEnvioRepository direccionEnvioRepository;
    private final UsuarioProfileRepository usuarioProfileRepository;

    public DireccionEnvioService(DireccionEnvioRepository direccionEnvioRepository,
                                 UsuarioProfileRepository usuarioProfileRepository) {
        this.direccionEnvioRepository = direccionEnvioRepository;
        this.usuarioProfileRepository = usuarioProfileRepository;
    }

    /**
     * Lista las direcciones del perfil identificado por azureSub.
     * Optional vacío si el perfil no existe.
     */
    @Transactional(readOnly = true)
    public Optional<List<DireccionEnvio>> listar(String azureSub) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .map(perfil -> direccionEnvioRepository.findByUsuarioProfileId(perfil.getId()));
    }

    /**
     * Crea una dirección para el perfil identificado por azureSub.
     * Si es predeterminada, desmarca las demás. Optional vacío si el perfil no existe.
     */
    public Optional<DireccionEnvio> crear(String azureSub, DireccionRequest request) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .map(perfil -> {
                    if (request.esPredeterminada()) {
                        limpiarPredeterminadas(perfil);
                    }
                    DireccionEnvio direccion = new DireccionEnvio(
                            perfil,
                            request.alias(),
                            request.calle(),
                            request.ciudad(),
                            request.estado(),
                            request.codigoPostal(),
                            request.pais());
                    direccion.setEsPredeterminada(request.esPredeterminada());
                    return direccionEnvioRepository.save(direccion);
                });
    }

    /**
     * Actualiza una dirección. Optional vacío si el perfil no existe o la dirección no le pertenece.
     */
    public Optional<DireccionEnvio> actualizar(String azureSub, UUID id, DireccionRequest request) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .flatMap(perfil -> direccionEnvioRepository.findById(id)
                        .filter(direccion -> direccion.getUsuarioProfile().getId().equals(perfil.getId()))
                        .map(direccion -> {
                            direccion.setAlias(request.alias());
                            direccion.setCalle(request.calle());
                            direccion.setCiudad(request.ciudad());
                            direccion.setEstado(request.estado());
                            direccion.setCodigoPostal(request.codigoPostal());
                            direccion.setPais(request.pais());
                            if (request.esPredeterminada() && !Boolean.TRUE.equals(direccion.getEsPredeterminada())) {
                                limpiarPredeterminadas(perfil);
                                direccion.setEsPredeterminada(true);
                            }
                            if (!request.esPredeterminada()) {
                                direccion.setEsPredeterminada(false);
                            }
                            return direccionEnvioRepository.save(direccion);
                        }));
    }

    /**
     * Elimina una dirección. Optional vacío si el perfil no existe o la dirección no le pertenece.
     */
    public Optional<DireccionEnvio> eliminar(String azureSub, UUID id) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .flatMap(perfil -> direccionEnvioRepository.findById(id)
                        .filter(direccion -> direccion.getUsuarioProfile().getId().equals(perfil.getId()))
                        .map(direccion -> {
                            direccionEnvioRepository.delete(direccion);
                            return direccion;
                        }));
    }

    /**
     * Marca una dirección como predeterminada (todas a false, la elegida a true).
     * Optional vacío si el perfil no existe o la dirección no le pertenece.
     */
    public Optional<DireccionEnvio> marcarPredeterminada(String azureSub, UUID id) {
        return usuarioProfileRepository.findByAzureSub(azureSub)
                .flatMap(perfil -> direccionEnvioRepository.findById(id)
                        .filter(direccion -> direccion.getUsuarioProfile().getId().equals(perfil.getId()))
                        .map(elegida -> {
                            limpiarPredeterminadas(perfil);
                            elegida.setEsPredeterminada(true);
                            return direccionEnvioRepository.save(elegida);
                        }));
    }

    private void limpiarPredeterminadas(UsuarioProfile perfil) {
        List<DireccionEnvio> direcciones = direccionEnvioRepository.findByUsuarioProfileId(perfil.getId());
        direcciones.forEach(direccion -> direccion.setEsPredeterminada(false));
        direccionEnvioRepository.saveAll(direcciones);
    }
}
