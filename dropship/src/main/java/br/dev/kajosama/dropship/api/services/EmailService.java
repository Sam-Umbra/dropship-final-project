package br.dev.kajosama.dropship.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    private static final String CONFIRMATION_EMAIL_TEMPLATE = """
        <div style="font-family: Arial, sans-serif; padding: 20px; text-align: center; border: 1px solid #ddd; border-radius: 8px; max-width: 600px; margin: auto;">
            <h2 style="color: #333;">Olá!</h2>
            <p style="color: #555;">Clique no botão abaixo para confirmar seu e-mail de %s:</p>
            
            <a href="%s" 
               style="display: inline-block;
                      padding: 12px 25px;
                      background-color: #FD6400;
                      color: white;
                      text-decoration: none;
                      border-radius: 6px;
                      font-size: 16px;
                      margin-top: 20px;
                      font-weight: bold;">
                Confirmar
            </a>
            
            <p style="margin-top: 30px; color: #888; font-size: 12px;">Se você não fez esta solicitação, por favor, ignore este e-mail.</p>
        </div>
    """;

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
        String htmlContent = CONFIRMATION_EMAIL_TEMPLATE.formatted(emailType, buttonUrl);
        sendHtmlEmail(to, subject, htmlContent);
    }
}
