package com.lumibooks.backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Maneja solicitudes que intentan acceder a recursos protegidos
 * sin una autenticación válida.
 *
 * Se ejecuta cuando Spring Security detecta:
 * - Usuario no autenticado.
 * - JWT inválido.
 * - JWT expirado.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Retorna una respuesta 401 personalizada en formato JSON.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        SecurityErrorResponse.write(
                response,
                HttpStatus.UNAUTHORIZED,
                "No autorizado",
                "No autenticado o token inválido o expirado"
        );
    }
}