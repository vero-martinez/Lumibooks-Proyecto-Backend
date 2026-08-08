package com.lumibooks.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.entity.RefreshToken;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.UnauthorizedException;
import com.lumibooks.backend.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    /**
     * Generador seguro de números aleatorios.
     *
     * Se utiliza para crear refresh tokens difíciles de predecir.
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    // Tiempo de vida del refresh token configurado en application.properties.
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Resultado de una rotación de refresh token.
     *
     * Devuelve:
     * - Usuario asociado para generar un nuevo Access Token.
     * - Refresh Token original para almacenarlo en la cookie.
     */
    public record RotateResult(User user, String rawToken) {}

    /**
     * Genera un refresh token aleatorio.
     *
     * El token real nunca se guarda en la base de datos,
     * solo su hash para evitar exponerlo.
     */
    public String generateRawToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    /**
     * Genera el hash SHA-256 del refresh token.
     *
     * La BD almacena únicamente este valor, no el token original.
     * Así, si la base de datos se filtra, los refresh tokens no quedan expuestos.
     */
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException("Error al hashear el token", e);
        }
    }

    /**
     * Crea el primer refresh token de una nueva sesión.
     *
     * Genera una familia nueva para relacionar futuras rotaciones del token.
     */
    @Transactional
    public String issue(User user) {
        return issue(user, UUID.randomUUID());
    }

    /**
     * Crea un refresh token dentro de una familia existente.
     *
     * Se utiliza durante la rotación del token para mantener
     * el historial de la sesión.
     */
    @Transactional
    public String issue(User user, UUID familyId) {

        String raw = generateRawToken();

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(raw))
                .familyId(familyId)
                .expiresAt(LocalDateTime.now()
                        .plus(Duration.ofMillis(refreshExpiration)))
                .build();

        refreshTokenRepository.save(token);

        return raw;
    }

    /**
     * Realiza la rotación de un refresh token.
     *
     * Valida el token actual, crea uno nuevo y marca el anterior
     * como reemplazado.
     *
     * Si se intenta reutilizar un token antiguo, se revoca toda la familia
     * porque puede indicar robo del refresh token.
     */
    @Transactional
    public RotateResult rotate(String rawToken) {

        RefreshToken old = refreshTokenRepository
                .findByTokenHash(hashToken(rawToken))
                .orElseThrow(() ->
                        new UnauthorizedException("Sesión inválida o expirada"));

        // Si ya fue reemplazado, alguien intenta reutilizar un token antiguo.
        if (old.getReplacedBy() != null) {
            refreshTokenRepository.revokeFamilyNow(old.getFamilyId());

            throw new UnauthorizedException(
                    "Se detectó un reuso del token de sesión");
        }

        if (old.isRevoked()) {
            throw new UnauthorizedException("Sesión cerrada");
        }

        if (old.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Sesión expirada");
        }

        if (!old.getUser().isActive()) {
            throw new UnauthorizedException("Cuenta desactivada");
        }

        // Crea un nuevo token dentro de la misma familia y reemplaza el anterior.
        String raw = generateRawToken();

        RefreshToken next = RefreshToken.builder()
                .user(old.getUser())
                .tokenHash(hashToken(raw))
                .familyId(old.getFamilyId())
                .expiresAt(LocalDateTime.now()
                        .plus(Duration.ofMillis(refreshExpiration)))
                .build();

        old.setReplacedBy(next.getTokenHash());

        refreshTokenRepository.saveAll(List.of(old, next));

        return new RotateResult(old.getUser(), raw);
    }

    /**
     * Revoca todos los refresh tokens pertenecientes a una familia.
     *
     * Se usa cuando se detecta reuso de token o cuando se desea
     * cerrar completamente una sesión.
     */
    @Transactional
    public void revokeFamily(UUID familyId) {
        refreshTokenRepository.findByFamilyId(familyId)
                .forEach(token -> token.setRevoked(true));
    }

    /**
     * Revoca todas las sesiones activas de un usuario.
     *
     * Permite cerrar sesión en todos los dispositivos.
     */
    @Transactional
    public void revokeAllByUser(Long userId) {
        refreshTokenRepository.findByUserIdAndRevokedFalse(userId)
                .forEach(token -> token.setRevoked(true));
    }

    /**
     * Busca la familia asociada a un refresh token y la revoca.
     *
     * Se utiliza principalmente durante el logout.
     */
    @Transactional
    public void revokeFamilyByToken(String rawToken) {

        refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .ifPresent(token -> revokeFamily(token.getFamilyId()));
    }
}