package com.jhonipereira.libraryapi.service.impl;

import com.jhonipereira.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message, List<String> mailList) {
//        4d86e405ec-c703cb@inbox.mailtrap.io
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Due book loan");
        mailMessage.setText(message);
        String[] mails = mailList.toArray(new String[mailList.size()]);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
