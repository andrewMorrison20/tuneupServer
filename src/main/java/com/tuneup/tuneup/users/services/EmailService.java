package com.tuneup.tuneup.users.services;

import com.tuneup.tuneup.users.dtos.EmailDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
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

    @Async("mailExecutor")
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

    public void sendFaqEmail(EmailDto emailDto) {
        String body = """
        New contact form submission:

        From: %s <%s>
        Subject: %s

        Message:
        %s
        """.formatted(
                emailDto.getName(),
                emailDto.getEmailAddress(),
                emailDto.getSubject(),
                emailDto.getMessage()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("tuneup.ad.confirm@gmail.com");
        message.setSubject("New Contact Message: " + emailDto.getSubject());
        message.setText(body);
        message.setFrom("noreply@yourdomain.com");
        message.setReplyTo(emailDto.getEmailAddress());

        mailSender.send(message);
    }
}
