package com.lumibooks.backend.service;

import com.lumibooks.backend.dto.auth.request.ForgotPasswordRequest;
import com.lumibooks.backend.dto.auth.request.ResetPasswordRequest;

/**
 * Servicio para la recuperación de contraseña.
 */
public interface PasswordResetService {

    /**
     * Solicita el envío de un código de recuperación al correo del usuario.
     *
     * @param request datos necesarios para solicitar la recuperación
     */
    void requestReset(ForgotPasswordRequest request);

    /**
     * Restablece la contraseña validando el código de recuperación recibido.
     *
     * @param request código de recuperación y nueva contraseña
     */
    void resetPassword(ResetPasswordRequest request);

}