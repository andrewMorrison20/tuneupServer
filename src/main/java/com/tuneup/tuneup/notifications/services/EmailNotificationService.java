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

    /**
     * Sends an email to a user detailing a notification event.
     * Executes in a shared thread pool
     * @param notification the details of the notification to include in the email.
     */
    @Async("mailExecutor")
    public void sendEmailNotification(NotificationDto notification) {
        String recipientEmail = getUserEmail(notification.getUserId());
        String subject = "Notification: " + notification.getType().toString();
        String text = notification.getMessage();

        emailService.sendEmail(recipientEmail, subject, text);
    }

    /**
     * retrieve an email address relating to a particular user by their id
     * @param userId the id of the user to get email address for
     * @return the email address of the user
     */
    private String getUserEmail(Long userId) {
        return appUserService.findById(userId).getEmail();
    }
}
