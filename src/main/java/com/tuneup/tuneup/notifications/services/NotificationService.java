// File: src/main/java/com/tuneup/tuneup/notifications/services/NotificationService.java
package com.tuneup.tuneup.notifications.services;

import com.tuneup.tuneup.notifications.Notification;
import com.tuneup.tuneup.notifications.NotificationMapper;
import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailNotificationService emailNotificationService;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate,
                               EmailNotificationService emailNotificationService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailNotificationService = emailNotificationService;
        this.notificationMapper = notificationMapper;
    }

    public NotificationDto createAndSendNotification(String type, String message, Long userId) {
        // Create and persist the notification
        Notification notification = new Notification();
        Notification savedNotification = notificationRepository.save(notification);

        // Map to DTO
        NotificationDto dto = notificationMapper.toDto(savedNotification);

        // Push via WebSocket using a user-specific destination.
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", dto);

        // Trigger asynchronous email sending.
        emailNotificationService.sendEmailNotification(dto);

        return dto;
    }
}

