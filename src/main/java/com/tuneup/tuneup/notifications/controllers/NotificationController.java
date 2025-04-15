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

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable Long userId) {
       return notificationRepository.findByUserIdAndReadFalse(userId);

    }

    @PostMapping("/{notificationId}/mark-read")
    public void markAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<Set<NotificationDto>> getUnreadNotifications(@PathVariable Long userId) {
        Set<NotificationDto> unreadDtos = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadDtos);
    }
}

