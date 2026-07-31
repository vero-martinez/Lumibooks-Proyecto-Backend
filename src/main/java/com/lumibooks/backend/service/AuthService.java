package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.AuthResult;

/**
 * Interfaz para la gestión de autenticación de usuarios.
 */
public interface AuthService {

    AuthResult register(RegisterRequest registerRequest);

    AuthResult login(LoginRequest loginRequest);

    AuthResult refresh(String rawRefreshToken);

    void logout(String accessToken, String rawRefreshToken);
}