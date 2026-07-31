package com.lumibooks.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumibooks.backend.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Buscar el token por su hash SHA-256 (para validarlo al refrescar)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Todos los tokens de una familia (para matar la familia si hay reuso)
    List<RefreshToken> findByFamilyId(UUID familyId);

    // Tokens no revocados de un usuario (para logout / limpiar sesiones)
    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

}