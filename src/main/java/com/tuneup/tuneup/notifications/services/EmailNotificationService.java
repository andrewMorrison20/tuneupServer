package com.tuneup.tuneup.notifications.services;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.users.services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final EmailService emailService;

    public EmailNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    public void sendEmailNotification(NotificationDto notification) {
        // Retrieve recipient email address using your user service or other logic.
        String recipientEmail = getUserEmail(notification.getUserId());
        // Customize the subject and message if necessary.
        String subject = "Notification: " + notification.getType();
        String text = notification.getMessage();
        // Use the existing email service to send the email.
        emailService.sendEmail(recipientEmail, subject, text);
    }

    private String getUserEmail(Long userId) {
        // Dummy implementation: replace with your user lookup logic.
        return "user" + userId + "@example.com";
    }
}
