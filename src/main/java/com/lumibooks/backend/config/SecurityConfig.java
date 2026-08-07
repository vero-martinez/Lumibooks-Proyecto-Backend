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
import com.lumibooks.backend.security.RateLimitFilter;
import com.lumibooks.backend.security.JwtAccessDeniedHandler;
import com.lumibooks.backend.security.JwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

/**
 * Configuración principal de seguridad de Spring Security.
 *
 * Define:
 * - Codificación de contraseñas.
 * - Gestión de autenticación.
 * - Configuración de autorización mediante roles.
 * - Integración de autenticación con JWT.
 * - Manejo personalizado de errores de seguridad.
 * - Configuración CORS y políticas HTTP.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Filtro encargado de validar los JWT enviados en las solicitudes.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Filtro encargado de limitar intentos en endpoints sensibles como login y refresh.
    private final RateLimitFilter rateLimitFilter;

    // Configuración CORS utilizada para permitir comunicación con el frontend.
    private final CorsConfigurationSource corsConfigurationSource;

    // Maneja errores cuando el usuario no está autenticado (401 Unauthorized).
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Maneja errores cuando el usuario no tiene permisos suficientes (403 Forbidden).
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    /**
     * Configura el codificador de contraseñas utilizando BCrypt.
     *
     * @return PasswordEncoder para cifrar y validar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCrypt aplica hashing seguro con salt automático.
        return new BCryptPasswordEncoder();
    }


    /**
     * Obtiene el AuthenticationManager utilizado por Spring Security
     * para realizar la autenticación de usuarios.
     *
     * @param config configuración de autenticación de Spring Security.
     * @return AuthenticationManager configurado.
     * @throws Exception si ocurre un error al obtener la configuración.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        // Obtener el AuthenticationManager configurado por Spring.
        return config.getAuthenticationManager();
    }


    /**
     * Configura la cadena de filtros y reglas de seguridad HTTP.
     *
     * Incluye:
     * - Configuración CORS.
     * - Deshabilitación de CSRF (API REST con JWT).
     * - Sesiones sin estado (STATELESS).
     * - Permisos según rutas y roles.
     * - Manejo personalizado de errores de seguridad.
     * - Registro de filtros personalizados.
     *
     * @param http configuración HTTP de Spring Security.
     * @return cadena de filtros configurada.
     * @throws Exception si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Aplicar configuración CORS.
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Deshabilitar CSRF porque la autenticación se maneja mediante JWT.
                .csrf(csrf -> csrf.disable())

                // Configurar aplicación sin sesiones almacenadas en servidor.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definir permisos de acceso según la ruta solicitada.
                .authorizeHttpRequests(authorize -> authorize

                        // Endpoints públicos como login, registro y refresh.
                        .requestMatchers("/api/public/**").permitAll()

                        // Endpoints exclusivos para administradores.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Endpoints exclusivos para clientes.
                        .requestMatchers("/api/client/**").hasRole("CLIENTE")

                        // Endpoints exclusivos para gestores.
                        .requestMatchers("/api/manager/**").hasRole("GESTOR")

                        // Cualquier otra ruta requiere autenticación.
                        .anyRequest().authenticated()
                )

                // Configurar respuestas personalizadas para errores de seguridad.
                .exceptionHandling(handling -> handling

                        // Usuario no autenticado → 401.
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                        // Usuario autenticado sin permisos → 403.
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );


        // Registrar el filtro JWT antes del filtro estándar de autenticación.
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);


        // Registrar rate limiting antes del filtro JWT para limitar intentos antes de validar tokens.
        http.addFilterBefore(
                rateLimitFilter,
                JwtAuthenticationFilter.class);


        return http.build();
    }
}