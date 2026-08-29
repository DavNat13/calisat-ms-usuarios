package com.califorge.msusuarios.repository;

import com.califorge.msusuarios.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, UUID> {

    List<DireccionEnvio> findByUsuarioProfileId(UUID usuarioProfileId);
}
