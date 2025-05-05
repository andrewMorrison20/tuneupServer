
package com.tuneup.tuneup.notifications.services;

import com.tuneup.tuneup.notifications.*;
import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.entities.Notification;
import com.tuneup.tuneup.notifications.enums.NotificationType;
import com.tuneup.tuneup.notifications.mappers.NotificationMapper;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailNotificationService emailNotificationService;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate,
                               EmailNotificationService emailNotificationService,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailNotificationService = emailNotificationService;
        this.notificationMapper = notificationMapper;
    }

    /*
    * Listens for a notification event and invokes create notification
     */
    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        createAndSendNotification(event.getNotificationType(), event.getMessage(), event.getUserId());
    }

    /**
     * Creates a notificaiton and saves it to the db, also broadcasts the notification via websockets
     * @param type the type of notification
     * @param message the message to include in the notification
     * @param userId the id of the user to notify
     * @return the notification as a dto
     */
    private NotificationDto createAndSendNotification(NotificationType type, String message, Long userId) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);

        NotificationDto dto = notificationMapper.toDto(savedNotification);

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", dto);

        // Trigger asynchronous email sending.
        emailNotificationService.sendEmailNotification(dto);

        return dto;
    }

    /**
     * Retrieves all unread notifications from the database for a given user
     * @param userId Id of the user to retrieve notifications for
     * @return the set of unread notifications
     */
    public Set<NotificationDto> getUnreadNotifications(Long userId){
       List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
       return notifications.stream().map(notificationMapper::toDto).collect(Collectors.toSet());

    }
}

