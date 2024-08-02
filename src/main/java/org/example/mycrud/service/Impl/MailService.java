package org.example.mycrud.service.Impl;

import org.example.mycrud.model.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;


    public void sendEmail(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.getTo());
        message.setSubject(mailBody.getSubject());
        message.setText(mailBody.getText().toString());

        javaMailSender.send(message);
    }
}
