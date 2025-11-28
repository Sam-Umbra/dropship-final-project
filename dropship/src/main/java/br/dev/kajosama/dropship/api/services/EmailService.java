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

/**
 * @author Sam_Umbra
 * @Description Service class for sending emails.
 *              It provides asynchronous methods to send HTML emails,
 *              including specific templates for account confirmation and
 *              supplier confirmation.
 *              It interacts with {@link JavaMailSender} to send emails and
 *              loads HTML templates from resources.
 */
@Service
public class EmailService {

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * The Spring MailSender instance used to send emails.
     */
    private final JavaMailSender mailSender;
    /**
     * The email address from which emails are sent, configured in application
     * properties.
     */
    private final String fromEmail;

    /**
     * Constructs an {@link EmailService} with the given {@link JavaMailSender} and
     * sender email address.
     *
     * @param mailSender The {@link JavaMailSender} instance.
     * @param fromEmail  The email address to be used as the sender.
     */
    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    /**
     * Sends an HTML email asynchronously.
     *
     * @param to          The recipient's email address.
     * @param subject     The subject of the email.
     * @param htmlContent The HTML content of the email body.
     * @throws IllegalStateException if there is a failure in sending the email.
     */
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

    /**
     * Sends an account confirmation email with a button linking to a confirmation
     * URL.
     * This method loads an HTML template and injects the confirmation URL and CSS.
     *
     * @param to        The recipient's email address.
     * @param subject   The subject of the email.
     * @param buttonUrl The URL for the confirmation button.
     * @param emailType A descriptive string for the type of email (e.g., "Conta da
     *                  Loja").
     * @throws IllegalStateException if there is a failure in loading the email
     *                               template.
     */
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

    /**
     * Sends a supplier confirmation email with specific supplier and user details.
     * This method loads a supplier-specific HTML template and injects various
     * details.
     *
     * @param to                The recipient's email address.
     * @param subject           The subject of the email.
     * @param buttonUrl         The URL for the confirmation button.
     * @param emailType         A descriptive string for the type of email (e.g.,
     *                          "Conta de Fornecedor").
     * @param supplierName      The name of the supplier.
     * @param supplierEmail     The email of the supplier.
     * @param supplierPhone     The phone number of the supplier.
     * @param supplierUserName  The name of the supplier's primary user.
     * @param supplierUserPhone The phone number of the supplier's primary user.
     * @param supplierId        The ID of the supplier.
     * @throws IllegalStateException if there is a failure in loading the email
     *                               template.
     */
    @Async
    public void sendSupplierEmail(String to, String subject, String buttonUrl, String emailType, String supplierName,
            String supplierEmail, String supplierPhone, String supplierUserName, String supplierUserPhone,
            String supplierId) {
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

    /**
     * Loads a resource file from the classpath as a String.
     *
     * @param path The path to the resource file (e.g.,
     *             "templates/email/email-style.css").
     * @return The content of the resource file as a String.
     * @throws IOException              if the resource is not found or an I/O error
     *                                  occurs while reading it.
     * @throws IllegalArgumentException if the resource stream is null.
     */
    private String loadResourceFile(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null)
            throw new IOException("Resource not found: " + path);
        byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
        return new String(binaryData, StandardCharsets.UTF_8);
    }
}
