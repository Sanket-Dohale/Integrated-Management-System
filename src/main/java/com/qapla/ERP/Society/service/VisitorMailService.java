

package com.qapla.ERP.Society.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VisitorMailService {

    @Autowired
    private JavaMailSender mailSender;

    // Method for sending HTML email with attachment
    public void sendMailWithAttachment(String to, String subject, String htmlBody, String pathToAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = HTML content

        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment("qrcode.png", file);

        mailSender.send(message);
    }

    // Simple plain text email sender used on check-in notification
    public void sendSimpleMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}



