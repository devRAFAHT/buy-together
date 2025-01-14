package com.github.devrafaht.userapi.domain.service.mailsender;

import com.github.devrafaht.userapi.domain.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailSenderManager {

    private final Map<String, IMailSender> mailSenders;

    public void sendMail(String senderType, String sender, String recipient, String subject, String body) {
        log.info("Executing mail sender for type: {} to recipient: {}", senderType, recipient);

        IMailSender mailSender = mailSenders.get(senderType);

        if (mailSender == null) {
            log.warn("No mail sender found for type: {}", senderType);
            throw new EmailSendingException("Mail sender not supported for type: " + senderType);
        }

        try {
            mailSender.sendMail(sender, recipient, subject, body);
            log.info("Mail sent successfully using senderType: {}", senderType);
        } catch (Exception e) {
            log.error("Failed to send mail using senderType: {}, error: {}", senderType, e.getMessage());
            throw new EmailSendingException(e.getMessage());
        }
    }
}