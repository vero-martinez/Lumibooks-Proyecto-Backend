package com.lumibooks.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumibooks.backend.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting para los endpoints de autenticación.
 *
 * Limita a 5 intentos por minuto por IP en las rutas de login y refresh
 * para prevenir fuerza bruta. Responde HTTP 429 al superar el límite.
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int CAPACITY = 5;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);
    private static final Duration IDLE_TIMEOUT = Duration.ofMinutes(10);
    private static final Set<String> RATE_LIMITED_PATHS = Set.of(
            "/api/public/auth/login",
            "/api/public/auth/refresh");

    private final Map<String, IpBucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (RATE_LIMITED_PATHS.contains(uri)) {
            String ip = request.getRemoteAddr();
            IpBucket ipBucket = buckets.computeIfAbsent(ip, this::newIpBucket);
            ipBucket.lastAccess = System.currentTimeMillis();

            if (!ipBucket.bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResponse.builder().message("Demasiados intentos, espera un minuto").build()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // Elimina los buckets de IPs inactivas para no acumular memoria
    @Scheduled(fixedDelay = 60000)
    public void cleanIdleBuckets() {
        long cutoff = System.currentTimeMillis() - IDLE_TIMEOUT.toMillis();
        buckets.entrySet().removeIf(entry -> entry.getValue().lastAccess < cutoff);
    }

    private IpBucket newIpBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.greedy(CAPACITY, REFILL_PERIOD));
        return new IpBucket(Bucket.builder().addLimit(limit).build());
    }

    // Bucket junto con la hora de su último uso (para la limpieza de inactivos)
    private static final class IpBucket {
        private final Bucket bucket;
        private volatile long lastAccess;

        private IpBucket(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccess = System.currentTimeMillis();
        }
    }
}
