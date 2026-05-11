package com.lumibooks.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Filtro encargado de autenticar usuarios mediante JWT.
 *
 * Intercepta cada request HTTP para:
 * - Extraer el token JWT del header Authorization.
 * - Validar el token.
 * - Obtener el usuario asociado al token.
 * - Registrar la autenticación en Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Servicio encargado de generar y validar tokens JWT.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Servicio encargado de cargar usuarios desde la base de datos.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Método ejecutado automáticamente en cada request HTTP.
     *
     * Realiza el proceso de autenticación basado en JWT.
     *
     * @param request request HTTP entrante
     * @param response response HTTP
     * @param filterChain cadena de filtros de Spring Security
     * @throws ServletException excepción relacionada con servlets
     * @throws IOException excepción de entrada/salida
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // Extraer token JWT desde el header Authorization
            String token = extractToken(request);

            // Validar token
            if (token != null &&
                    jwtTokenProvider.validateToken(token)) {

                // Obtener email almacenado en el token
                String email =
                        jwtTokenProvider.getEmailFromToken(token);

                // Cargar usuario desde base de datos
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // Crear autenticación para Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Agregar detalles adicionales de la request
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Registrar usuario autenticado en el contexto de seguridad
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            logger.error(
                    "No se pudo establecer la autenticación del usuario: "
                            + e.getMessage()
            );
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT desde el header Authorization.
     *
     * El formato esperado es:
     * Bearer TOKEN
     *
     * @param request request HTTP
     * @return token JWT o null si no existe
     */
    private String extractToken(HttpServletRequest request) {

        String headerAuth =
                request.getHeader("Authorization");

        // Verificar que el header tenga formato Bearer TOKEN
        if (headerAuth != null &&
                headerAuth.startsWith("Bearer ")) {

            // Remover "Bearer " y retornar solo el token
            return headerAuth.substring(7);
        }

        return null;
    }
}