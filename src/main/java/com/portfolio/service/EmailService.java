package com.portfolio.service;

import com.portfolio.entity.ContactMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.contact.recipient-email}")
    private String recipientEmail;

    @Async
    public void sendContactNotification(ContactMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("[Portfolio Contact] " + message.getSubject());
            helper.setText(buildContactEmailHtml(message), true);

            mailSender.send(mime);
            log.info("Contact notification sent for message id: {}", message.getId());
        } catch (MessagingException ex) {
            log.error("Failed to send contact email: {}", ex.getMessage());
        }
    }

    @Async
    public void sendAutoReply(ContactMessage message) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setTo(message.getSenderEmail());
            helper.setSubject("Thank you for reaching out!");
            helper.setText(buildAutoReplyHtml(message.getSenderName()), true);

            mailSender.send(mime);
            log.info("Auto-reply sent to: {}", message.getSenderEmail());
        } catch (MessagingException ex) {
            log.error("Failed to send auto-reply: {}", ex.getMessage());
        }
    }

    private String buildContactEmailHtml(ContactMessage msg) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px;">
                    <h2 style="color: #333;">New Contact Message</h2>
                    <table style="width:100%; border-collapse: collapse;">
                        <tr><td style="padding:8px; font-weight:bold;">From:</td>
                            <td style="padding:8px;">%s (%s)</td></tr>
                        <tr><td style="padding:8px; font-weight:bold;">Subject:</td>
                            <td style="padding:8px;">%s</td></tr>
                        <tr><td style="padding:8px; font-weight:bold; vertical-align:top;">Message:</td>
                            <td style="padding:8px; white-space:pre-wrap;">%s</td></tr>
                    </table>
                </div>
                """.formatted(
                msg.getSenderName(), msg.getSenderEmail(),
                msg.getSubject() != null ? msg.getSubject() : "(no subject)",
                msg.getMessage()
        );
    }

    private String buildAutoReplyHtml(String name) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px;">
                    <h2>Hi %s,</h2>
                    <p>Thank you for reaching out! I've received your message and will get back to you as soon as possible.</p>
                    <p>Best regards</p>
                </div>
                """.formatted(name);
    }
}