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

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * Filtro encargado de autenticar usuarios mediante JWT.
 *
 * Se ejecuta en cada petición HTTP antes de llegar a los controladores.
 *
 * Flujo:
 * 1. Obtiene el JWT del header Authorization.
 * 2. Valida firma y expiración del token.
 * 3. Verifica que el token no esté revocado en Redis.
 * 4. Comprueba que el usuario siga activo y que la versión del token coincida.
 * 5. Registra la autenticación en el contexto de Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        // Servicio encargado de generar y validar tokens JWT.
        private final JwtTokenProvider jwtTokenProvider;

        // Servicio encargado de cargar usuarios desde la base de datos.
        private final UserDetailsService userDetailsService;

        
        // Servicio encargado de la blacklist de tokens JWT en Redis.
        private final TokenBlacklistService tokenBlacklistService;

        // Repositorio de usuarios para validar tokenVersion y estado activo.
        private final UserRepository userRepository;

        /**
         * Método ejecutado automáticamente en cada request HTTP.
         *
         * Realiza el proceso de autenticación basado en JWT.
         *
         * @param request     request HTTP entrante
         * @param response    response HTTP
         * @param filterChain cadena de filtros de Spring Security
         * @throws ServletException excepción relacionada con servlets
         * @throws IOException      excepción de entrada/salida
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

                        // Validar firma y expiración del token
                        if (token != null &&
                                        jwtTokenProvider.validateToken(token)) {

                                // Rechazar tokens revocados en la blacklist de Redis
                                String jti = jwtTokenProvider.getJtiFromToken(token);

                                if (!tokenBlacklistService.isBlacklisted(jti)) {

                                        // Obtener email almacenado en el token
                                        String email = jwtTokenProvider.getEmailFromToken(token);

                                        // Cargar usuario desde base de datos para validar su estado
                                        User user = userRepository.findByEmail(email).orElse(null);

                                        // Validar cuenta activa y que el tokenVersion coincida
                                        if (user != null && user.isActive()
                                                        && Objects.equals(user.getTokenVersion(),
                                                                        jwtTokenProvider.getTokenVersionFromToken(
                                                                                        token))) {

                                                // Cargar detalles de usuario para Spring Security
                                                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                                                // Crear autenticación para Spring Security
                                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                                userDetails,
                                                                null,
                                                                userDetails.getAuthorities());

                                                // Agregar detalles adicionales de la request
                                                authentication.setDetails(
                                                                new WebAuthenticationDetailsSource()
                                                                                .buildDetails(request));

                                                // Registrar usuario autenticado en el contexto de seguridad
                                                SecurityContextHolder.getContext()
                                                                .setAuthentication(authentication);
                                        }
                                }
                        }

                } catch (Exception e) {

                        logger.error(
                                        "No se pudo establecer la autenticación del usuario: "
                                                        + e.getMessage());
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

                String headerAuth = request.getHeader("Authorization");

                // Verificar que el header tenga formato Bearer TOKEN
                if (headerAuth != null &&
                                headerAuth.startsWith("Bearer ")) {

                        // Remover "Bearer " y retornar solo el token
                        return headerAuth.substring(7);
                }

                return null;
        }
}