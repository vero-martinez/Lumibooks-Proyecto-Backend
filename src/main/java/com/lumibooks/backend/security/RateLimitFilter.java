package com.lumibooks.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting para los endpoints de autenticación.
 *
 * Limita a 5 intentos por minuto por IP en las rutas de login y refresh
 * para prevenir fuerza bruta. Responde HTTP 429 al superar el límite.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int CAPACITY = 5;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.endsWith("/login") || uri.endsWith("/refresh")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);

            if (!bucket.tryConsume(1)) {
                response.setStatus(429); // HTTP 429 Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Demasiados intentos, espera un minuto\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.greedy(CAPACITY, REFILL_PERIOD));
        return Bucket.builder().addLimit(limit).build();
    }
}
