package com.tuneup.tuneup.notifications.services;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.services.EmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final EmailService emailService;
    private final AppUserService appUserService;

    public EmailNotificationService(EmailService emailService, AppUserService appUserService) {
        this.emailService = emailService;
        this.appUserService = appUserService;
    }

    @Async
    public void sendEmailNotification(NotificationDto notification) {
        String recipientEmail = getUserEmail(notification.getUserId());
        String subject = "Notification: " + notification.getType().toString();
        String text = notification.getMessage();

        emailService.sendEmail(recipientEmail, subject, text);
    }

    private String getUserEmail(Long userId) {
        return appUserService.findById(userId).getEmail();
    }
}
