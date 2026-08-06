package com.lumibooks.backend.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilidad para construir respuestas de error relacionadas con seguridad.
 *
 * Permite devolver errores de autenticación en formato JSON cuando Spring
 * Security
 * bloquea una petición antes de llegar al controlador.
 */
public final class SecurityErrorResponse {

        // Conversor utilizado para transformar objetos Java a JSON.
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        // Evita crear instancias de esta clase utilitaria.
        private SecurityErrorResponse() {
        }

        /**
         * Estructura estándar de respuesta de error.
         */
        private record ErrorBody(
                        String timestamp,
                        int status,
                        String error,
                        String message) {
        }

        /**
         * Escribe una respuesta HTTP de error en formato JSON.
         *
         * Se utiliza principalmente para errores de autenticación y autorización.
         */
        public static void write(
                        HttpServletResponse response,
                        HttpStatus status,
                        String error,
                        String message)
                        throws IOException {

                response.setStatus(status.value());
                response.setContentType("application/json;charset=UTF-8");

                ErrorBody body = new ErrorBody(
                                LocalDateTime.now().toString(),
                                status.value(),
                                error,
                                message);

                OBJECT_MAPPER.writeValue(response.getWriter(), body);
        }
}