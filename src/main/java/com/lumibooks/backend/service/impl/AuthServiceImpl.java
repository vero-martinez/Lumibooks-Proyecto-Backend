package com.lumibooks.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.auth.request.LoginRequest;
import com.lumibooks.backend.dto.auth.request.RegisterRequest;
import com.lumibooks.backend.dto.auth.response.AuthResponse;
import com.lumibooks.backend.dto.auth.response.AuthResult;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.enums.RoleUser;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.exception.UnauthorizedException;
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
 *
 * Gestiona el registro, inicio de sesión, renovación y cierre
 * de sesión de los usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Repositorio para acceder a los usuarios.
    private final UserRepository userRepository;

    // Codificador de contraseñas.
    private final PasswordEncoder passwordEncoder;

    // Encargado de autenticar las credenciales del usuario.
    private final AuthenticationManager authenticationManager;

    // Generador y validador de Access Tokens JWT.
    private final JwtTokenProvider jwtTokenProvider;

    // Gestión de Refresh Tokens.
    private final RefreshTokenService refreshTokenService;

    // Blacklist de Access Tokens en Redis.
    private final TokenBlacklistService tokenBlacklistService;

    // Gestión de suscripciones al newsletter.
    private final SubscriberService subscriberService;

    // Envío de notificaciones a los usuarios.
    private final NotificationService notificationService;

    // Tiempo de vida del Access Token.
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Registrar un nuevo usuario e iniciar su sesión.
    @Override
    @Transactional
    public AuthResult register(RegisterRequest registerRequest) {

        // Verificar que el email no esté registrado.
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Verificar que el DNI no esté registrado.
        if (userRepository.existsByDni(registerRequest.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }

        // Crear y guardar el nuevo usuario.
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

        // Suscribir al usuario al newsletter si lo solicitó durante el registro.
        if (Boolean.TRUE.equals(registerRequest.getSubscribedToNewsletter())) {
            subscriberService.subscribeFromRegister(savedUser);
        }

        // Enviar una notificación de bienvenida.
        notificationService.sendNotification(
                savedUser,
                "¡Bienvenido a LumiBooks!",
                "Hola " + savedUser.getFullName()
                        + ", gracias por unirte a LumiBooks. ¡Esperamos que disfrutes tu experiencia!");

        return buildAuthResult(savedUser, "Usuario registrado exitosamente");
    }

    // Autenticar a un usuario con sus credenciales.
    @Override
    @Transactional
    public AuthResult login(LoginRequest loginRequest) {

        // Mismo mensaje para email inexistente y contraseña incorrecta
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email o contraseña incorrectos"));

        // Validar las credenciales del usuario.
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));
        } catch (Exception e) {
            throw new UnauthorizedException("Email o contraseña incorrectos");
        }

        return buildAuthResult(user, "Inicio de sesión exitoso");
    }

    // Renovar la sesión utilizando un Refresh Token válido.
    @Override
    @Transactional
    public AuthResult refresh(String rawRefreshToken) {

        // Rotar el Refresh Token y obtener uno nuevo.
        RotateResult rotated = refreshTokenService.rotate(rawRefreshToken);

        return buildAuthResult(rotated.user(), rotated.rawToken(), "Sesión renovada");
    }

    // Cerrar la sesión e invalidar los tokens del usuario.
    @Override
    @Transactional
    public void logout(String accessToken, String rawRefreshToken) {

        // Agregar el Access Token a la blacklist para impedir su uso hasta que expire.
        if (accessToken != null && !accessToken.isBlank()) {
            try {
                if (jwtTokenProvider.validateToken(accessToken)) {
                    tokenBlacklistService.blacklist(
                            jwtTokenProvider.getJtiFromToken(accessToken),
                            jwtExpiration);
                }
            } catch (Exception ignored) {
                // Si el token ya expiró o es inválido, no es necesario agregarlo a la blacklist.
            }
        }

        // Revocar la familia del Refresh Token para impedir futuras renovaciones de la sesión.
        if (rawRefreshToken != null && !rawRefreshToken.isBlank()) {
            refreshTokenService.revokeFamilyByToken(rawRefreshToken);
        }
    }

    // Generar un Access Token y un Refresh Token para una nueva sesión.
    private AuthResult buildAuthResult(User user, String message) {
        return buildAuthResult(user, refreshTokenService.issue(user), message);
    }

    // Generar un Access Token utilizando un Refresh Token ya emitido.
    private AuthResult buildAuthResult(User user, String rawRefreshToken, String message) {

        String accessToken = jwtTokenProvider.generateToken(user);

        // Construir la respuesta que se enviará al cliente.
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