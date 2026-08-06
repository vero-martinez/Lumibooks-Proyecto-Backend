package com.lumibooks.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Componente encargado de crear y validar tokens JWT.
 *
 * Permite generar tokens para usuarios autenticados y obtener
 * información almacenada dentro del token, como email, rol y versión.
 */
@Component
public class JwtTokenProvider {

    // Clave secreta utilizada para firmar y verificar los JWT.
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de vida del Access Token en milisegundos.
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Clave criptográfica utilizada para firmar los tokens.
    private SecretKey key;

    /**
     * Inicializa la clave criptográfica usada para firmar y validar JWT.
     *
     * Convierte el secreto configurado en application.properties
     * en una clave HMAC que será utilizada al generar y verificar tokens.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un Access Token JWT para un usuario autenticado.
     *
     * El token incluye:
     * - Email del usuario como identificador principal.
     * - Rol para controlar permisos.
     * - Token version para invalidar sesiones anteriores.
     * - JTI para identificar el token individualmente.
     */
    public String generateToken(User user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("tokenVersion", user.getTokenVersion())
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Obtiene el email almacenado dentro del JWT.
     *
     * @throws UnauthorizedException si el token es inválido o expiró.
     */
    public String getEmailFromToken(String token) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();

        } catch (Exception e) {

            throw new UnauthorizedException(
                    "Token inválido o expirado");
        }
    }

    /**
     * Obtiene el identificador único del JWT (JTI).
     *
     * Se utiliza para identificar un token específico,
     * por ejemplo al manejar revocaciones.
     */
    public String getJtiFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getId();

        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }
    }

    /**
     * Obtiene la versión del token almacenada dentro del JWT.
     *
     * Permite comprobar si el token pertenece a la versión actual
     * del usuario o si fue invalidado por un cambio de versión.
     */
    public Integer getTokenVersionFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("tokenVersion", Integer.class);

        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }
    }

    /**
     * Verifica si un JWT es válido.
     *
     * Comprueba:
     * - Que la firma sea correcta.
     * - Que el token no haya expirado.
     * - Que el contenido no haya sido alterado.
     */
    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}