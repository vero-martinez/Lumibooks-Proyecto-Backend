package com.lumibooks.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.request.LoginRequest;
import com.lumibooks.backend.dto.request.RegisterRequest;
import com.lumibooks.backend.dto.response.AuthResponse;
import com.lumibooks.backend.dto.response.AuthResult;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.ResourceNotFoundException;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.security.JwtTokenProvider;
import com.lumibooks.backend.security.RefreshTokenService;
import com.lumibooks.backend.security.RefreshTokenService.RotateResult;
import com.lumibooks.backend.security.TokenBlacklistService;
import com.lumibooks.backend.service.AuthService;
import com.lumibooks.backend.service.NotificationService;
import com.lumibooks.backend.service.SubscriberService;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SubscriberService subscriberService;
    private final NotificationService notificationService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    @Transactional
    public AuthResult register(RegisterRequest registerRequest) {

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

        // Suscribe al newsletter si el usuario lo solicitó
        if (Boolean.TRUE.equals(registerRequest.getSubscribedToNewsletter())) {
            subscriberService.subscribeFromRegister(savedUser);
        }

        notificationService.sendNotification(
                savedUser,
                "¡Bienvenido a LumiBooks!",
                "Hola " + savedUser.getFullName()
                        + ", gracias por unirte a LumiBooks. ¡Esperamos que disfrutes tu experiencia!");

        return buildAuthResult(savedUser, "Usuario registrado exitosamente");
    }

    @Override
    @Transactional
    public AuthResult login(LoginRequest loginRequest) {

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

        return buildAuthResult(user, "Inicio de sesión exitoso");
    }

    @Override
    @Transactional
    public AuthResult refresh(String rawRefreshToken) {

        RotateResult rotated = refreshTokenService.rotate(rawRefreshToken);

        return buildAuthResult(rotated.user(), rotated.rawToken(), "Sesión renovada");
    }

    @Override
    @Transactional
    public void logout(String accessToken, String rawRefreshToken) {

        // 1. Matar el access token actual (blacklist de jti en Redis)
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                if (jwtTokenProvider.validateToken(accessToken)) {
                    tokenBlacklistService.blacklist(
                            jwtTokenProvider.getJtiFromToken(accessToken),
                            jwtExpiration
                    );
                }
            } catch (Exception ignored) {
                // token inválido/expirado: la blacklist es solo para el logout instantáneo
            }
        }

        // 2. Revocar la familia del refresh token
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenService.revokeFamilyByToken(rawRefreshToken);
        }
    }

    // Genera access + refresh (nueva familia) y arma la respuesta
    private AuthResult buildAuthResult(User user, String message) {
        return buildAuthResult(user, refreshTokenService.issue(user), message);
    }

    // Genera access + arma la respuesta con un refresh ya emitido (rotación)
    private AuthResult buildAuthResult(User user, String rawRefreshToken, String message) {
        String accessToken = jwtTokenProvider.generateToken(user);

        AuthResponse response = AuthResponse.builder()
                .token(accessToken)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .message(message)
                .build();

        return new AuthResult(response, rawRefreshToken);
    }
}