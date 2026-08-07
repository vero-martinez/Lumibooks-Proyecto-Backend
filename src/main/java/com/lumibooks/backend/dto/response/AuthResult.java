package com.lumibooks.backend.dto.response;

/**
 * Resultado del proceso de autenticación.
 *
 * Contiene la respuesta que se enviará al cliente y el Refresh Token
 * generado, que posteriormente se almacena en una cookie HttpOnly.
 */
public record AuthResult(

        // Datos que se enviarán en el cuerpo de la respuesta HTTP.
        AuthResponse response,

        // Refresh Token en texto plano, utilizado para crear la cookie HttpOnly.
        String rawRefreshToken

) {}