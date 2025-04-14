package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.users.dtos.EmailDto;
import com.tuneup.tuneup.users.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @Mock
    private JavaMailSenderImpl mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendResetEmail() {
        String email = "reset@example.com";
        String resetUrl = "http://reset-link.com";

        emailService.sendResetEmail(email, resetUrl);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        assertArrayEquals(new String[]{email}, message.getTo());
        assertEquals("Password Reset Request", message.getSubject());
        assertEquals("Click the link to reset your password: " + resetUrl, message.getText());
    }

    @Test
    void testSendVerificationEmail() {
        String email = "verify@example.com";
        String verificationUrl = "http://verify-link.com";

        emailService.sendVerificationEmail(email, verificationUrl);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        assertArrayEquals(new String[]{email}, message.getTo());
        assertEquals("Account Verification", message.getSubject());
        assertEquals("Click the link to verify your account: " + verificationUrl, message.getText());
    }

    @Test
    void testSendEmail() {
        String recipientEmail = "user@example.com";
        String subject = "Test Subject";
        String text = "Test message content.";

        emailService.sendEmail(recipientEmail, subject, text);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        assertArrayEquals(new String[]{recipientEmail}, message.getTo());
        assertEquals(subject, message.getSubject());
        assertEquals(text, message.getText());
    }

    @Test
    void testSendFaqEmail() {

        EmailDto emailDto = new EmailDto();
        emailDto.setName("John Doe");
        emailDto.setEmailAddress("john@example.com");
        emailDto.setSubject("Test FAQ Subject");
        emailDto.setMessage("This is a test message.");

        emailService.sendFaqEmail(emailDto);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage message = messageCaptor.getValue();

        String expectedBody = """
                New contact form submission:
                
                From: John Doe <john@example.com>
                Subject: Test FAQ Subject
                
                Message:
                This is a test message.
                """.stripIndent().trim();

        assertEquals(expectedBody, message.getText().trim());

        assertArrayEquals(new String[]{"tuneup.ad.confirm@gmail.com"}, message.getTo());
        assertEquals("New Contact Message: " + emailDto.getSubject(), message.getSubject());
        assertEquals("noreply@yourdomain.com", message.getFrom());
        assertEquals(emailDto.getEmailAddress(), message.getReplyTo());
    }
}
