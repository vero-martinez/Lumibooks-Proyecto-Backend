package com.lumibooks.backend.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.AuthResponse;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * Implementación de la interfaz AuthService para gestionar la autenticación de usuarios.
 * Funciones principales:
 * - Registrar nuevos usuarios con validación de email y DNI únicos.
 * - Autenticar usuarios existentes mediante email y contraseña.
 * - Generar respuestas de autenticación con datos del usuario y mensajes de confirmación.
 */
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository; // Repositorio para acceder a los datos de los usuarios en la base de datos
    private final PasswordEncoder passwordEncoder; // Para encriptar las contraseñas de los usuarios
    private final AuthenticationManager authenticationManager; // Para autenticar a los usuarios durante el login

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        
        // Validar que el email no esté registrado
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }
        // Validar que el DNI no esté registrado
        if (userRepository.existsByDni(registerRequest.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }
        
        // Crear un nuevo usuario con los datos proporcionados y el rol CLIENTE
        User newUser = User.builder()
                .nombre(registerRequest.getNombre())
                .apellido(registerRequest.getApellido())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .dni(registerRequest.getDni())
                .telefono(registerRequest.getTelefono())
                .rol(RoleUser.CLIENTE)
                .activo(true)
                .build();

        // Guardar el nuevo usuario en la base de datos
        User savedUser = userRepository.save(newUser);
        
        // Retornar una respuesta de autenticación con los datos del usuario registrado
        return AuthResponse.builder()
                .token(null)
                .nombre(savedUser.getNombre())
                .apellido(savedUser.getApellido())
                .email(savedUser.getEmail())
                .rol(savedUser.getRol().toString())
                .mensaje("Usuario registrado exitosamente")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        
        // Buscar el usuario por email, lanzar excepción si no se encuentra
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Intentar autenticar al usuario con el email y contraseña proporcionados si la autenticación falla, lanzar excepción
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadRequestException("Email o contraseña incorrectos");
        }
        
        // Retornar una respuesta de autenticación con los datos del usuario autenticado
        return AuthResponse.builder()
                .token(null)
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .rol(user.getRol().toString())
                .mensaje("Inicio de sesión exitoso")
                .build();
    }
}