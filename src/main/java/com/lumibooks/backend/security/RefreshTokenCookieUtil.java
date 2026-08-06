package com.lumibooks.backend.security;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utilidad encargada de gestionar la cookie del Refresh Token.
 *
 * Crea, elimina y obtiene el refresh token almacenado en una cookie
 * httpOnly para evitar exponerlo directamente al JavaScript del navegador.
 */
@Component
public class RefreshTokenCookieUtil {

    // Nombre de la cookie donde se almacena el refresh token.
    public static final String COOKIE_NAME = "refresh_token";

    // Ruta donde la cookie estará disponible.
    public static final String COOKIE_PATH = "/api/public/auth";

    // Tiempo de vida del refresh token.
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // Define si la cookie solo debe enviarse mediante HTTPS.
    @Value("${cookie.secure}")
    private boolean cookieSecure;

    /**
     * Crea la cookie httpOnly con el refresh token.
     *
     * httpOnly evita que el token pueda ser leído mediante JavaScript,
     * reduciendo el riesgo ante ataques XSS.
     */
    public void addRefreshCookie(HttpServletResponse response, String rawToken) {

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, rawToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(Duration.ofMillis(refreshExpiration))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Elimina la cookie del refresh token.
     *
     * Se utiliza al cerrar sesión para borrar la sesión almacenada
     * en el navegador.
     */
    public void clearCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * Obtiene el refresh token enviado por el navegador.
     *
     * Busca la cookie por su nombre y retorna su valor.
     * Si no existe una cookie válida, retorna null.
     */
    public String extractRefreshToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}