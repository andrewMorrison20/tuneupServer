
package com.tuneup.tuneup.notifications.services;

import com.tuneup.tuneup.notifications.Notification;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.NotificationMapper;
import com.tuneup.tuneup.notifications.NotificationType;
import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


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

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        createAndSendNotification(event.getNotificationType(), event.getMessage(), event.getUserId());
    }

    private NotificationDto createAndSendNotification(NotificationType type, String message, Long userId) {

        Notification notification = new Notification();
        Notification savedNotification = notificationRepository.save(notification);

        NotificationDto dto = notificationMapper.toDto(savedNotification);

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", dto);

        // Trigger asynchronous email sending.
        emailNotificationService.sendEmailNotification(dto);

        return dto;
    }
}

