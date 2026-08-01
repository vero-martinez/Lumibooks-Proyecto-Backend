package com.lumibooks.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Buscar el token por su hash SHA-256 (para validarlo al refrescar)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Todos los tokens de una familia (para matar la familia si hay reuso)
    List<RefreshToken> findByFamilyId(UUID familyId);

    // Tokens no revocados de un usuario (para logout / limpiar sesiones)
    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    // Borrar tokens ya expirados (limpieza diaria)
    void deleteByExpiresAtBefore(LocalDateTime now);

    // Borrar tokens revocados que tengan más de 1 día (limpieza diaria)
    void deleteByRevokedTrueAndCreatedAtBefore(LocalDateTime cutoff);

    // Revocar una familia en transacción independiente (se confirma aunque la operación falle)
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.familyId = :familyId")
    int revokeFamilyNow(@Param("familyId") UUID familyId);

}