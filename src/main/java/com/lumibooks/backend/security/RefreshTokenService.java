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

    // Generador criptográficamente seguro (no Random común, que es predecible)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Resultado de una rotación: el usuario (para firmar el nuevo access) y el token raw (para la cookie)
    public record RotateResult(User user, String rawToken) {}

    // Genera el token opaco: 48 bytes aleatorios en base64url (sin padding)
    public String generateRawToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Huella SHA-256 del token (lo que se guarda en BD, nunca el token)
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Error al hashear el token", e);
        }
    }

    // Emite el PRIMER token de una familia (al loguearse/registrarse)
    @Transactional
    public String issue(User user) {
        return issue(user, UUID.randomUUID());
    }

    // Emite un token en una familia existente (para rotación)
    @Transactional
    public String issue(User user, UUID familyId) {
        String raw = generateRawToken();
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(raw))
                .familyId(familyId)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                .build();
        refreshTokenRepository.save(token);
        return raw;
    }

    // Rota el token (se llama al usar /refresh)
    @Transactional
    public RotateResult rotate(String rawToken) {
        RefreshToken old = refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new UnauthorizedException("Sesión inválida o expirada"));

        // ¿Este token YA fue reemplazado? => alguien lo está reusando (robo) => matar la familia
        // La revocación va en transacción independiente para que el rollback del 401 no la deshaga
        if (old.getReplacedBy() != null) {
            refreshTokenRepository.revokeFamilyNow(old.getFamilyId());
            throw new UnauthorizedException("Se detectó un reuso del token de sesión");
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

        // Crear el hijo en la misma familia y marcar el padre como reemplazado
        String raw = generateRawToken();
        RefreshToken next = RefreshToken.builder()
                .user(old.getUser())
                .tokenHash(hashToken(raw))
                .familyId(old.getFamilyId())
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                .build();
        old.setReplacedBy(next.getTokenHash());
        refreshTokenRepository.saveAll(List.of(old, next));

        return new RotateResult(old.getUser(), raw);
    }

    // Mata toda la familia (reuso detectado o logout total)
    @Transactional
    public void revokeFamily(UUID familyId) {
        refreshTokenRepository.findByFamilyId(familyId)
                .forEach(token -> token.setRevoked(true));
    }

    // Revoca todas las sesiones de un usuario (logout en todos los dispositivos)
    @Transactional
    public void revokeAllByUser(Long userId) {
        refreshTokenRepository.findByUserIdAndRevokedFalse(userId)
                .forEach(token -> token.setRevoked(true));
    }

    // Encuentra la familia del token (por su hash) y la revoca (para logout)
    @Transactional
    public void revokeFamilyByToken(String rawToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawToken))
                .ifPresent(token -> revokeFamily(token.getFamilyId()));
    }
}