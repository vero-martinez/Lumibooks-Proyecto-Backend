package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.AuthResult;

/**
 * Define las operaciones relacionadas con la autenticación de usuarios.
 */
public interface AuthService {

    // Registrar un nuevo usuario e iniciar su sesión.
    AuthResult register(RegisterRequest registerRequest);

    // Autenticar a un usuario con sus credenciales.
    AuthResult login(LoginRequest loginRequest);

    // Generar un nuevo Access Token utilizando un Refresh Token válido.
    AuthResult refresh(String rawRefreshToken);

    // Cerrar la sesión del usuario e invalidar sus tokens.
    void logout(String accessToken, String rawRefreshToken);
}