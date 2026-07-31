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
 * Permite definir qué orígenes, métodos HTTP y encabezados
 * pueden acceder al backend desde aplicaciones frontend externas.
 *
 * Esta configuración es necesaria cuando el frontend y backend
 * se ejecutan en dominios o puertos diferentes.
 */
@Configuration
public class CorsConfig {

        @Value("${app.cors.allowed-origins}")
        private List<String> allowedOrigins;

        /**
         * Define la configuración CORS global de la aplicación.
         *
         * @return configuración CORS utilizada por Spring Security
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                // Crear configuración CORS
                CorsConfiguration configuration = new CorsConfiguration();

                // Permitir peticiones desde el frontend (definido por perfil: dev/prod)
                configuration.setAllowedOrigins(allowedOrigins);

                // Permitir métodos HTTP específicos
                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                // Permitir todos los encabezados HTTP
                configuration.setAllowedHeaders(List.of("*"));

                // Permitir envío de credenciales y tokens
                configuration.setAllowCredentials(true);

                // Tiempo en segundos que el navegador almacenará la configuración CORS, para no preguntar en cada solicitud
                configuration.setMaxAge(3600L);

                // Aplicar configuración a todas las rutas de la aplicación
                UrlBasedCorsConfigurationSource source = 
                        new UrlBasedCorsConfigurationSource();

                // Registrar la configuración CORS para todas las rutas
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}