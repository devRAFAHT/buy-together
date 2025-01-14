package com.github.devrafaht.userapi.domain.service.mailsender;

public interface IMailSender {

    public void sendMail(String sender, String recipient, String subject, String message);

}
