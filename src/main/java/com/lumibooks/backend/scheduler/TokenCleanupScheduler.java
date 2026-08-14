package com.lumibooks.backend.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.repository.PasswordResetTokenRepository;
import com.lumibooks.backend.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

/**
 * Job programado que limpia periódicamente tokens vencidos o revocados
 * de la base de datos, para que no queden acumulando espacio indefinidamente.
 */
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    // Días de gracia para tokens revocados antes de eliminarlos.
    private static final int REVOKED_GRACE_DAYS = 1;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    // Todos los días a las 3:00 AM (hora del servidor)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanExpiredTokens() {

        LocalDateTime now = LocalDateTime.now();

        // Borra refresh tokens vencidos (ya no sirven para generar nuevos access tokens).
        refreshTokenRepository.deleteByExpiresAtBefore(now);

        // Borra refresh tokens que fueron revocados y ya pasó el período de gracia definido arriba.
        refreshTokenRepository.deleteByRevokedTrueAndCreatedAtBefore(
                now.minusDays(REVOKED_GRACE_DAYS));
        
        // Borra códigos de recuperación de contraseña que ya expiraron.
        passwordResetTokenRepository.deleteByExpiresAtBefore(now);
        
    }
}