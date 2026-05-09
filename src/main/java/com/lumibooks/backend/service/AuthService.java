package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.AuthResponse;

/**
 * Interfaz para la gestión de autenticación de usuarios.
 */
public interface AuthService {

    /**
     * Autentica un usuario con email y contraseña
     * @param loginRequest contiene email y password
     * @return AuthResponse con token y datos del usuario
     * @throws BadRequestException si credenciales son inválidas
     * @throws ResourceNotFoundException si usuario no existe
     */
    AuthResponse register (RegisterRequest registerRequest);

    /**
     * Registra un nuevo usuario
     * @param registerRequest contiene datos del nuevo usuario
     * @return AuthResponse con token y datos del usuario creado
     * @throws BadRequestException si email o DNI ya existen
     */
    AuthResponse login (LoginRequest loginRequest);
}
