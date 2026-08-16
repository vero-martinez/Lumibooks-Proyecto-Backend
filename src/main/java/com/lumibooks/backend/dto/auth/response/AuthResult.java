package com.lumibooks.backend.dto.auth.response;

/**
 * Resultado interno del proceso de autenticación.
 *
 * Contiene la respuesta que se enviará al cliente y el Refresh Token
 * generado por el backend. El token se mantiene en texto plano únicamente
 * para que pueda ser utilizado posteriormente para crear la cookie HttpOnly.
 */
public record AuthResult(

        // Datos de autenticación que formarán el cuerpo de la respuesta HTTP.
        AuthResponse response,

        // Refresh Token generado durante la autenticación, utilizado para crear
        // la cookie HttpOnly que se enviará al cliente.
        String rawRefreshToken

) {}