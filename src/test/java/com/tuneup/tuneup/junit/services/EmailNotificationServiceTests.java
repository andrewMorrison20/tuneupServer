package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.enums.NotificationType;
import com.tuneup.tuneup.notifications.services.EmailNotificationService;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import com.tuneup.tuneup.users.services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTests {

    @Mock
    private EmailService emailService;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    private NotificationDto notificationDto;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto();
        notificationDto.setUserId(5L);
        notificationDto.setType(NotificationType.NEW_PAYMENT);
        notificationDto.setMessage("Test message");

        appUser = new AppUser();
        appUser.setId(5L);
        appUser.setEmail("user@example.com");
    }

    @Test
    void sendEmailNotification_SendsEmailWithCorrectParameters() {
        when(appUserService.findById(5L)).thenReturn(appUser);

        emailNotificationService.sendEmailNotification(notificationDto);

        verify(appUserService).findById(5L);
        verify(emailService).sendEmail(
                "user@example.com",
                "Notification: NEW_PAYMENT",
                "Test message"
        );
        verifyNoMoreInteractions(emailService, appUserService);
    }
}
