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

/**
 * Repositorio para gestionar los Refresh Tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Busca un Refresh Token por su hash.
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Obtiene todos los Refresh Tokens de una misma familia.
    List<RefreshToken> findByFamilyId(UUID familyId);

    // Obtiene los Refresh Tokens activos de un usuario.
    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    // Elimina los Refresh Tokens que ya expiraron (borrado masivo en un solo SQL).
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now")
    int deleteByExpiresAtBefore(@Param("now") LocalDateTime now);

    // Elimina los Refresh Tokens revocados con más de un día de antigüedad.
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.revoked = true AND t.createdAt < :cutoff")
    int deleteByRevokedTrueAndCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);

    /**
     * Revoca todos los Refresh Tokens de una misma familia.
     *
     * Se ejecuta en una transacción independiente para asegurar
     * que el cambio se confirme incluso si la operación principal falla.
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE RefreshToken t SET t.revoked = true WHERE t.familyId = :familyId")
    int revokeFamilyNow(@Param("familyId") UUID familyId);

}