package com.tuneup.tuneup.notifications.controllers;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.entities.Notification;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import com.tuneup.tuneup.notifications.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository, NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    /**
     * Find all notifications in the db for a given user
     * @param userId the id of the user to find notifications for
     * @return List Notification - the list of unread notifications
     */
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
       return notificationRepository.findByUserIdAndReadFalse(userId);

    }

    /**
     * Mark a notification as read
     * @param notificationId the id of the notification to mark as read
     */
    @PostMapping("/{notificationId}/mark-read")
    public void markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Get all unread notifications for a given user
     * @param userId the id of the user to grab notifications for
     * @return Set NotificationDto- the set of unread notifications
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Set<NotificationDto>> getUnreadNotifications(@PathVariable Long userId) {
        Set<NotificationDto> unreadDtos = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadDtos);
    }
}

