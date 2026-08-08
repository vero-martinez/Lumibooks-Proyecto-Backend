package com.lumibooks.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
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
 * Filtro encargado de limitar la cantidad de solicitudes
 * realizadas a los endpoints sensibles de autenticación.
 *
 * Utiliza el algoritmo Token Bucket mediante Bucket4j para evitar
 * ataques de fuerza bruta limitando intentos por dirección IP.
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    // Cantidad máxima de solicitudes permitidas dentro del período definido.
    private static final int CAPACITY = 5;

    // Tiempo en el que se regeneran los intentos disponibles.
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    // Tiempo máximo de inactividad antes de eliminar el registro de una IP.
    private static final Duration IDLE_TIMEOUT = Duration.ofMinutes(10);

    // Rutas donde se aplicará el límite de solicitudes.
    private static final Set<String> RATE_LIMITED_PATHS = Set.of(
            "/api/public/auth/login",
            "/api/public/auth/refresh");

    // Almacena un bucket independiente para cada dirección IP.
    private final Map<String, IpBucket> buckets = new ConcurrentHashMap<>();

    // Convierte objetos Java a JSON para enviar respuestas HTTP.
    private final ObjectMapper objectMapper;

    // Interceptar cada solicitud HTTP y validar el límite de intentos.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Verificar si la ruta actual requiere protección contra demasiados intentos.
        if (RATE_LIMITED_PATHS.contains(uri)) {

            // Obtener la IP del cliente que realiza la solicitud.
            String ip = request.getRemoteAddr();

            // Crear o recuperar el bucket asociado a la IP.
            IpBucket ipBucket = buckets.computeIfAbsent(ip, this::newIpBucket);

            // Actualizar la fecha del último uso para la limpieza posterior.
            ipBucket.lastAccess = System.currentTimeMillis();

            // Consumir un intento del bucket.
            // Si no quedan tokens disponibles, se bloquea la solicitud.
            if (!ipBucket.bucket.tryConsume(1)) {

                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json;charset=UTF-8");

                response.getWriter().write(
                        objectMapper.writeValueAsString(
                                ApiResponse.builder()
                                        .message("Demasiados intentos, espera un minuto")
                                        .build()));

                return;
            }
        }

        // Continuar con el siguiente filtro o la ejecución del endpoint.
        filterChain.doFilter(request, response);
    }

    // Limpia los buckets asociados a IPs que ya no realizan solicitudes.
    // Evita acumular datos innecesarios en memoria.
    @Scheduled(fixedDelay = 60000)
    public void cleanIdleBuckets() {

        long cutoff = System.currentTimeMillis() - IDLE_TIMEOUT.toMillis();

        buckets.entrySet()
                .removeIf(entry -> entry.getValue().lastAccess < cutoff);
    }

    // Crear un nuevo límite de solicitudes para una dirección IP.
    private IpBucket newIpBucket(String ip) {

        // Configurar el límite de intentos y la regeneración de tokens.
        Bandwidth limit = Bandwidth.builder()
                .capacity(CAPACITY)
                .refillGreedy(CAPACITY, REFILL_PERIOD)
                .build();

        return new IpBucket(
                Bucket.builder()
                        .addLimit(limit)
                        .build());
    }

    /**
     * Representa el límite de solicitudes asociado a una IP.
     *
     * Contiene:
     * - Bucket: controla la cantidad de intentos disponibles.
     * - lastAccess: registra el último uso para poder eliminar IPs inactivas.
     */
    private static final class IpBucket {

        // Bucket4j encargado de controlar los intentos disponibles.
        private final Bucket bucket;

        // Último momento en que la IP realizó una solicitud.
        private volatile long lastAccess;

        private IpBucket(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccess = System.currentTimeMillis();
        }
    }
}