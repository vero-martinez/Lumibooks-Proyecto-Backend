package com.lumibooks.backend.security;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "jti:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String jti, long ttlMillis) {
        redisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", Duration.ofMillis(ttlMillis));
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + jti));
    }
}