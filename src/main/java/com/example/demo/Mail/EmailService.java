package com.example.demo.Mail;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(System.getenv().get("MAIL_USERNAME"));
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(mensaje);

        javaMailSender.send(mailMessage);
    }

    public void enviarCorreo(String destinatario, String asunto, String mensaje, String adjunto) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(System.getenv().get("MAIL_USERNAME"));
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(mensaje);
            FileSystemResource file = new FileSystemResource(new File(adjunto));
            helper.addAttachment(file.getFilename(), file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}