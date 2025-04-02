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


    public void sendVerificationEmail(String email, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Account Verification");
        message.setText("Click the link to verify your account: " + verificationUrl);
        mailSender.send(message);
    }



    public void sendEmail(String recipientEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
