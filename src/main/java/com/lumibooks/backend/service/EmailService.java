package com.lumibooks.backend.service;

/**
 * Interfaz para el envío de correos electrónicos.
 */
public interface EmailService {

    /**
     * Envía un código de recuperación de contraseña al correo del usuario.
     *
     * @param toEmail correo electrónico del destinatario
     * @param firstName nombre del usuario
     * @param code código de recuperación
     * @param expirationMinutes minutos de vigencia del código
     */
    void sendPasswordResetCode(
            String toEmail,
            String firstName,
            String code,
            int expirationMinutes);
}