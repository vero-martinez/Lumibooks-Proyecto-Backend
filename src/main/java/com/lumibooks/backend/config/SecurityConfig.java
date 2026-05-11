package com.lumibooks.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
/**
 * Esta clase configura la seguridad de la aplicación utilizando Spring Security. 
 * Ya que se encarga de definir cómo se autentican los usuarios, qué rutas están protegidas y cómo se manejan las sesiones.
 * Funciones principales:
 * - Configurar el PasswordEncoder para encriptar las contraseñas de los usuarios.
 * - Configurar el AuthenticationManager para gestionar la autenticación de los usuarios.
 * - Configurar la cadena de filtros de seguridad para proteger las rutas de la aplicación.
 */
public class SecurityConfig {
    
    @Bean
    // Configurar el PasswordEncoder para encriptar las contraseñas de los usuarios
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Configurar el AuthenticationManager para gestionar la autenticación de los usuarios
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    // Configurar la cadena de filtros de seguridad para proteger las rutas de la aplicación
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Configurar CORS con la configuración definida en CorsConfig
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF ya que se usará JWT para la autenticación
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configurar la gestión de sesiones para que sea sin estado (stateless)
                .authorizeHttpRequests(authorize -> authorize // Permitir el acceso a las rutas de autenticación sin necesidad de estar autenticado
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}