package com.lumibooks.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configura las políticas CORS de la aplicación.
 *
 * Permite controlar qué aplicaciones frontend pueden comunicarse
 * con el backend cuando se ejecutan en diferentes dominios o puertos.
 */
@Configuration
public class CorsConfig {

        // Orígenes permitidos para realizar peticiones al backend.
        // Se obtiene según el perfil activo (dev/prod).
        @Value("${app.cors.allowed-origins}")
        private List<String> allowedOrigins;

        // Crear la configuración CORS utilizada por Spring Security.
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                // Crear configuración de políticas CORS.
                CorsConfiguration configuration = new CorsConfiguration();

                // Permitir peticiones desde los frontend configurados.
                configuration.setAllowedOrigins(allowedOrigins);

                // Permitir los métodos HTTP utilizados por la aplicación.
                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                // Permitir todos los encabezados HTTP enviados en las solicitudes.
                configuration.setAllowedHeaders(List.of("*"));

                // Permitir el envío de cookies y credenciales en las peticiones.
                configuration.setAllowCredentials(true);

                // Tiempo que el navegador almacena la configuración CORS
                // antes de realizar nuevamente una solicitud preflight.
                configuration.setMaxAge(3600L);

                // Aplicar la configuración CORS a todas las rutas del backend.
                UrlBasedCorsConfigurationSource source =
                                new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}