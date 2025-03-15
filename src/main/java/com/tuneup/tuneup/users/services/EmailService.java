package com.tuneup.tuneup.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
public class EmailService {


    private final JavaMailSenderImpl mailSender;

    public EmailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetEmail(String email, String resetUrl) {
     SimpleMailMessage message = new SimpleMailMessage();
     message.setTo(email);
     message.setSubject("Password Reset Request");
     message.setText("Click the link to reset your password: " + resetUrl);
     mailSender.send(message);
  }

    public void notifyChatMessage(String recipientEmail, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("New Chat Message");
        message.setText("You have a new message: " + messageContent);
        mailSender.send(message);
    }
}
