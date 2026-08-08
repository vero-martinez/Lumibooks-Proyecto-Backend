package com.lumibooks.backend.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private static final int REVOKED_GRACE_DAYS = 1;

    private final RefreshTokenRepository refreshTokenRepository;

    // Todos los días a las 3:00 AM (hora del servidor)
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        refreshTokenRepository.deleteByRevokedTrueAndCreatedAtBefore(
                LocalDateTime.now().minusDays(REVOKED_GRACE_DAYS));
    }
}
