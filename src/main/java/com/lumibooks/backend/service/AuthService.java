package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.auth.request.LoginRequest;
import com.lumibooks.backend.dto.auth.request.RegisterRequest;
import com.lumibooks.backend.dto.auth.response.AuthResult;

/**
 * Interfaz para la autenticación de usuarios.
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario e inicia su sesión.
     *
     * @param registerRequest datos necesarios para el registro
     * @return resultado de autenticación con los tokens generados
     */
    AuthResult register(RegisterRequest registerRequest);

    /**
     * Autentica a un usuario mediante sus credenciales.
     *
     * @param loginRequest credenciales del usuario
     * @return resultado de autenticación con los tokens generados
     */
    AuthResult login(LoginRequest loginRequest);

    /**
     * Genera un nuevo Access Token utilizando un Refresh Token válido.
     *
     * @param rawRefreshToken Refresh Token del usuario
     * @return resultado de autenticación con el nuevo Access Token
     */
    AuthResult refresh(String rawRefreshToken);

    /**
     * Cierra la sesión del usuario e invalida sus tokens.
     *
     * @param accessToken Access Token de la sesión actual
     * @param rawRefreshToken Refresh Token de la sesión actual
     */
    void logout(String accessToken, String rawRefreshToken);
    
}