package com.lumibooks.backend.security;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Servicio encargado de gestionar tokens JWT revocados.
 *
 * Utiliza Redis como lista negra temporal para almacenar los JTI
 * de tokens que ya no deben aceptarse (por ejemplo, después de un logout).
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    // Prefijo utilizado para diferenciar las claves de JWT revocados en Redis.
    private static final String KEY_PREFIX = "jti:blacklist:";

    private final StringRedisTemplate redisTemplate;

    /**
     * Guarda un JTI en Redis hasta que expire el tiempo de vida del token.
     *
     * Después de ese tiempo el token ya habría expirado naturalmente,
     * por lo que no es necesario mantenerlo en la lista negra.
     */
    public void blacklist(String jti, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + jti, "1", Duration.ofMillis(ttlMillis));
    }

    /**
     * Verifica si un JTI pertenece a un token revocado.
     *
     * Retorna true si el token fue agregado previamente a la lista negra.
     */
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(KEY_PREFIX + jti)
        );
    }
}