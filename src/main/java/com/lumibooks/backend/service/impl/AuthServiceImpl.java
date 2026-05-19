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
import com.lumibooks.backend.security.JwtTokenProvider;
import com.lumibooks.backend.service.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de autenticación.
 * Se encarga del registro de usuarios,
 * validación de credenciales y generación de tokens JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest datos del usuario a registrar
     * @return respuesta con token JWT y datos del usuario registrado
     * @throws BadRequestException si el email o DNI ya existen
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        if (userRepository.existsByDni(registerRequest.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }

        User newUser = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .dni(registerRequest.getDni())
                .cellphone(registerRequest.getCellphone())
                .role(RoleUser.CLIENTE)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);

        // Genera el token JWT del usuario registrado
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .message("Usuario registrado exitosamente")
                .build();
    }

    /**
     * Autentica un usuario existente.
     *
     * @param loginRequest credenciales del usuario
     * @return respuesta con token JWT y datos del usuario autenticado
     * @throws ResourceNotFoundException si el usuario no existe
     * @throws BadRequestException si las credenciales son incorrectas
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

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

        // Genera el token JWT del usuario autenticado
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .message("Inicio de sesión exitoso")
                .build();
    }
}