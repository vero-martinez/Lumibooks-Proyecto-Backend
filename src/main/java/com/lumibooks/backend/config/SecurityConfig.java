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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.lumibooks.backend.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de seguridad de la aplicación.
 * 
 * Define:
 * - Encriptación de contraseñas
 * - Manejo de autenticación
 * - Configuración de JWT
 * - Políticas de seguridad HTTP
 * - Configuración CORS
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Configura el codificador de contraseñas utilizando BCrypt.
     *
     * @return instancia de PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proporciona el AuthenticationManager utilizado por Spring Security
     * para autenticar usuarios.
     *
     * @param config configuración de autenticación de Spring
     * @return AuthenticationManager configurado
     * @throws Exception si ocurre un error al obtener el AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     *
     * Características principales:
     * - Habilita CORS
     * - Deshabilita CSRF
     * - Usa sesiones STATELESS para JWT
     * - Permite acceso libre a rutas de autenticación
     * - Requiere autenticación para el resto de endpoints
     * - Agrega el filtro JWT antes del filtro de autenticación estándar
     *
     * @param http configuración HTTP de Spring Security
     * @return SecurityFilterChain configurado
     * @throws Exception si ocurre un error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        // Agregar JwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}