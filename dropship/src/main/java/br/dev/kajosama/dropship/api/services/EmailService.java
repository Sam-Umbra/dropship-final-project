package br.dev.kajosama.dropship.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Falha ao enviar e-mail para {}: {}", to, e.getMessage());
            throw new IllegalStateException("Falha ao enviar o e-mail", e);
        }
    }

    @Async
    public void sendEmailWithConfirmationButton(String to, String subject, String buttonUrl, String emailType) {
        try {
            String css = loadResourceFile("templates/email/email-style.css");
            String htmlTemplate = loadResourceFile("templates/email/confirmation-email.html");

            String htmlContent = htmlTemplate
                    .replace("{{css}}", css)
                    .replace("{{buttonUrl}}", buttonUrl);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (IOException e) {
            logger.error("Falha ao carregar template de e-mail: {}", e.getMessage());
            throw new IllegalStateException("Falha ao carregar template de e-mail", e);
        }
    }

    @Async
    public void sendSupplierEmail(String to, String subject, String buttonUrl, String emailType, String supplierName, String supplierEmail, String supplierPhone, String supplierUserName, String supplierUserPhone, String supplierId) {
        try {
            String htmlTemplate = loadResourceFile("templates/email/supplier-confirmation-email.html");

            String htmlContent = htmlTemplate
                    .replace("{{buttonUrl}}", buttonUrl)
                    .replace("{{supplierName}}", supplierName)
                    .replace("{{supplierEmail}}", supplierEmail)
                    .replace("{{supplierPhone}}", supplierPhone)
                    .replace("{{supplierUserName}}", supplierUserName)
                    .replace("{{supplierUserPhone}}", supplierUserPhone)
                    .replace("{{supplierId}}", supplierId);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (IOException e) {
            logger.error("Falha ao carregar template de e-mail: {}", e.getMessage());
            throw new IllegalStateException("Falha ao carregar template de e-mail", e);
        }
    }

    private String loadResourceFile(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) throw new IOException("Resource not found: " + path);
        byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
        return new String(binaryData, StandardCharsets.UTF_8);
    }
}
