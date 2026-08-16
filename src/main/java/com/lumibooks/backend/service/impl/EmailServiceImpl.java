package com.lumibooks.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.lumibooks.backend.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    // Dirección desde la cual se envían los correos.
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetCode(
            String toEmail,
            String firstName,
            String code,
            int expirationMinutes) {
        try {
            // Crear el mensaje con soporte para contenido HTML y UTF-8.
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Código de recuperación de contraseña - Orbyss");
            helper.setText(buildHtmlBody(firstName, code, expirationMinutes), true);

            // Enviar el correo.
            mailSender.send(message);

            log.debug("Código de recuperación enviado a {}", toEmail);
        } catch (MessagingException e) {
            throw new IllegalStateException("No se pudo enviar el código de recuperación", e);
        } catch (MailException e) {
            log.error("Fallo al enviar código de recuperación a {}", toEmail, e);
            throw new IllegalStateException("No se pudo enviar el código de recuperación", e);
        }
    }

    // Construye el contenido HTML del correo de recuperación.
    private String buildHtmlBody(String firstName, String code, int expirationMinutes) {
        String safeFirstName = HtmlUtils.htmlEscape(firstName);

        return """
                <html>
                <body style="margin: 0; padding: 0; background-color: #f0f1f8; font-family: Arial, Helvetica, sans-serif;">
                    <div style="max-width: 480px; margin: 0 auto; padding: 24px;">
                        <div style="background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06);">

                            <!-- Header -->
                            <div style="background: linear-gradient(135deg, #4338ca, #6366f1); padding: 28px 32px;">
                                <h1 style="margin: 0; color: #ffffff; font-size: 20px; letter-spacing: 0.5px;">Orbyss</h1>
                            </div>

                            <!-- Body -->
                            <div style="padding: 32px;">
                                <h2 style="margin-top: 0; margin-bottom: 16px; color: #1a1a2e; font-size: 20px;">Recuperación de contraseña</h2>
                                <p style="color: #444; font-size: 15px; line-height: 1.5;">Hola <strong>%s</strong>,</p>
                                <p style="color: #444; font-size: 15px; line-height: 1.5;">Recibimos una solicitud para restablecer tu contraseña. Usa el siguiente código para continuar:</p>

                                <div style="text-align: center; background-color: #eef0fd; border: 1px dashed #6366f1; border-radius: 8px; padding: 20px; margin: 28px 0;">
                                    <span style="font-size: 34px; font-weight: bold; letter-spacing: 10px; color: #4338ca;">%s</span>
                                </div>

                                <p style="color: #444; font-size: 14px; line-height: 1.5;">
                                    Este código es válido por <strong>%d minutos</strong>.
                                </p>
                                <p style="color: #888; font-size: 13px; line-height: 1.5;">
                                    Si no solicitaste este cambio, puedes ignorar este correo con seguridad.
                                </p>
                            </div>

                            <!-- Footer -->
                            <div style="background-color: #f7f7fb; padding: 16px 32px; text-align: center;">
                                <p style="margin: 0; color: #aaa; font-size: 12px;">© %d Orbyss. Todos los derechos reservados.</p>
                            </div>

                        </div>
                    </div>
                </body>
                </html>
                """.formatted(safeFirstName, code, expirationMinutes, java.time.Year.now().getValue());
    }
}