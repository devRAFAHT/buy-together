package com.github.devrafaht.userapi.domain.service.mailsender;

import com.github.devrafaht.userapi.domain.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("JavaMailSender")
@RequiredArgsConstructor
@Slf4j
public class JavaMailSenderService implements IMailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(String sender, String recipient, String subject, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(recipient);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.error("Error sending email from {} to {} with subject '{}': {}", sender, recipient, subject, e.getMessage(), e);
            throw new EmailSendingException("Error sending email to " + recipient + ". Details: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending email from {} to {} with subject '{}': {}", sender, recipient, subject, e.getMessage());
            throw new EmailSendingException("Unexpected error sending email to " + recipient + ". Details: " + e.getMessage());
        }
    }
}