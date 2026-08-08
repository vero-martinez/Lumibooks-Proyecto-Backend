package com.lumibooks.backend.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
/**
 * Manejador personalizado para errores de autorización.
 *
 * Se ejecuta cuando un usuario está autenticado pero no tiene
 * los permisos necesarios para acceder a un recurso protegido.
 */
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // Manejar errores de acceso denegado (403 Forbidden).
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        // Retornar una respuesta JSON personalizada indicando falta de permisos.
        SecurityErrorResponse.write(
                response,
                HttpStatus.FORBIDDEN,
                "Prohibido",
                "No tienes permisos para acceder a este recurso");
    }
}