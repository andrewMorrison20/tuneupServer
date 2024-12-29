package com.tuneup.tuneup.junit.services;



import com.tuneup.tuneup.users.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSenderImpl mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendResetEmail_ShouldSendEmail() {

        String testEmail = "test@example.com";
        String resetUrl = "http://example.com/reset-password";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendResetEmail(testEmail, resetUrl);
        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(testEmail, sentMessage.getTo()[0]);
        assertEquals("Password Reset Request", sentMessage.getSubject());
        assertEquals("Click the link to reset your password: " + resetUrl, sentMessage.getText());
    }
}
