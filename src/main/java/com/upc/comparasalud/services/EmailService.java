package com.upc.comparasalud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@comparasalud.com}")
    private String fromEmail;

    // HU06 – Verificación de correo
    public void sendVerificationEmail(String toEmail, String token) {
        String link = baseUrl + "/api/auth/verify-email?token=" + token;
        String body = "Hola,\n\n"
                + "Por favor verifica tu cuenta haciendo clic en el siguiente enlace:\n\n"
                + link + "\n\n"
                + "Este enlace expira en 24 horas.\n\n"
                + "Si no creaste esta cuenta, ignora este correo.\n\n"
                + "ComparaSalud";
        sendEmail(toEmail, "Verifica tu cuenta en ComparaSalud", body);
    }

    // HU03 – Recuperación de contraseña
    public void sendPasswordResetEmail(String toEmail, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        String body = "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Haz clic en el siguiente enlace (válido por 1 hora):\n\n"
                + link + "\n\n"
                + "Si no solicitaste esto, ignora este correo.\n\n"
                + "ComparaSalud";
        sendEmail(toEmail, "Restablece tu contraseña en ComparaSalud", body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            // Log only – avoids crash when mail is not configured in dev
            System.out.printf("[EMAIL MOCK] To: %s | Subject: %s%n", to, subject);
            System.out.println(body);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}