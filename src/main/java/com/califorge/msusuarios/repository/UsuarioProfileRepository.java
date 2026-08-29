package com.califorge.msusuarios.repository;

import com.califorge.msusuarios.model.UsuarioProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioProfileRepository extends JpaRepository<UsuarioProfile, UUID> {

    Optional<UsuarioProfile> findByAzureSub(String azureSub);

    boolean existsByAzureSub(String azureSub);
}
