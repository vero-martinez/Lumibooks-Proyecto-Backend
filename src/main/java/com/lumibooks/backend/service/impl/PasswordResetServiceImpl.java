package com.lumibooks.backend.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumibooks.backend.dto.auth.request.ForgotPasswordRequest;
import com.lumibooks.backend.dto.auth.request.ResetPasswordRequest;
import com.lumibooks.backend.entity.PasswordResetToken;
import com.lumibooks.backend.entity.User;
import com.lumibooks.backend.exception.BadRequestException;
import com.lumibooks.backend.repository.PasswordResetTokenRepository;
import com.lumibooks.backend.repository.UserRepository;
import com.lumibooks.backend.security.RefreshTokenService;
import com.lumibooks.backend.service.EmailService;
import com.lumibooks.backend.service.PasswordResetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    // Generador seguro para los códigos de recuperación.
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    // Tiempo de vigencia del código en minutos.
    @Value("${app.reset-code.expiration-minutes}")
    private int expirationMinutes;

    // Número máximo de intentos permitidos.
    @Value("${app.reset-code.max-attempts}")
    private int maxAttempts;

    @Override
    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // No revelar si el correo está registrado.
        if (user == null || !user.isActive()) {
            return;
        }

        // Invalidar cualquier código anterior.
        passwordResetTokenRepository.deleteByUserId(user.getId());

        // Generar y guardar un nuevo código de recuperación.
        String code = generateCode();

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .codeHash(passwordEncoder.encode(code))
                .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                .build();

        passwordResetTokenRepository.save(token);

        // Enviar el código al correo del usuario.
        try {
            emailService.sendPasswordResetCode(
                    user.getEmail(),
                    user.getFirstName(),
                    code,
                    expirationMinutes);
        } catch (IllegalStateException e) {
            log.debug(
                    "No se pudo enviar el código de recuperación a {}: {}",
                    user.getEmail(),
                    e.getMessage());
        }
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(
                        "El código es inválido o ha expirado"));

        // No permitir restablecer la contraseña de un usuario desactivado.
        if (!user.isActive()) {
            throw new BadRequestException("El código es inválido o ha expirado");
        }

        PasswordResetToken token = passwordResetTokenRepository
                .findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new BadRequestException(
                        "El código es inválido o ha expirado"));

        // Verificar que el código siga vigente.
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El código es inválido o ha expirado");
        }

        // Verificar que el código recibido coincida con el almacenado.
        if (!passwordEncoder.matches(request.getCode(), token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);

            // Invalidar el código al alcanzar el límite de intentos.
            if (token.getAttempts() >= maxAttempts) {
                token.setUsed(true);
            }

            passwordResetTokenRepository.save(token);

            throw new BadRequestException("El código es inválido o ha expirado");
        }

        // Marcar el código como utilizado.
        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        // Actualizar la contraseña e invalidar las sesiones anteriores.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        refreshTokenService.revokeAllByUser(user.getId());
    }

    // Genera un código numérico aleatorio de 6 dígitos.
    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}