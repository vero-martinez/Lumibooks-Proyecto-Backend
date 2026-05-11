package com.lumibooks.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lumibooks.backend.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Clase encargada de gestionar operaciones relacionadas con JWT.
 *
 * Funciones principales:
 * - Generar tokens JWT para usuarios autenticados.
 * - Extraer información del token, como el email.
 * - Validar si un token es válido o ha expirado.
 */
@Component
public class JwtTokenProvider {

    /**
     * Clave secreta utilizada para firmar y verificar los tokens JWT.
     * Se obtiene desde application.properties.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Se obtiene desde application.properties.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Clave criptográfica utilizada para firmar los JWT.
     */
    private SecretKey key;

    /**
     * Inicializa la clave criptográfica a partir del secret configurado.
     * Se ejecuta automáticamente después de crear el bean.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Genera un token JWT para el usuario autenticado.
     *
     * @param email email del usuario autenticado
     * @return token JWT generado
     */
    public String generateToken(String email) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Obtiene el email almacenado dentro del token JWT.
     *
     * @param token token JWT
     * @return email almacenado en el token
     * @throws UnauthorizedException si el token es inválido o expiró
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
                    "Token inválido o expirado"
            );
        }
    }

    /**
     * Valida si un token JWT es válido.
     *
     * Verifica:
     * - firma correcta
     * - token no expirado
     * - token no alterado
     *
     * @param token token JWT
     * @return true si el token es válido, false en caso contrario
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