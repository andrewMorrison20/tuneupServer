package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.notifications.controllers.NotificationController;
import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.entities.Notification;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import com.tuneup.tuneup.notifications.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification notif1;
    private Notification notif2;

    @BeforeEach
    void setUp() {
        notif1 = new Notification();
        notif1.setId(1L);
        notif1.setUserId(100L);
        notif1.setRead(false);

        notif2 = new Notification();
        notif2.setId(2L);
        notif2.setUserId(100L);
        notif2.setRead(false);
    }

    @Test
    void testGetNotificationsReturnsList() {
        List<Notification> list = Arrays.asList(notif1, notif2);
        when(notificationRepository.findByUserIdAndReadFalse(100L)).thenReturn(list);

        List<Notification> result = notificationController.getNotifications(100L);

        assertEquals(list, result);
        verify(notificationRepository).findByUserIdAndReadFalse(100L);
    }

    @Test
    void testGetNotificationsEmptyList() {
        when(notificationRepository.findByUserIdAndReadFalse(200L)).thenReturn(Collections.emptyList());

        List<Notification> result = notificationController.getNotifications(200L);

        assertTrue(result.isEmpty());
        verify(notificationRepository).findByUserIdAndReadFalse(200L);
    }

    @Test
    void testMarkAsReadSuccess() {
        Notification n = new Notification();
        n.setId(5L);
        n.setRead(false);

        when(notificationRepository.findById(5L)).thenReturn(Optional.of(n));
        when(notificationRepository.save(n)).thenReturn(n);

        notificationController.markAsRead(5L);

        assertTrue(n.isRead());
        verify(notificationRepository).findById(5L);
        verify(notificationRepository).save(n);
    }

    @Test
    void testMarkAsReadNotFound() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationController.markAsRead(10L));
        assertEquals("Notification not found", ex.getMessage());
        verify(notificationRepository).findById(10L);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetUnreadNotificationsReturnsSet() {
        NotificationDto dto1 = new NotificationDto();
        dto1.setId(1L);
        NotificationDto dto2 = new NotificationDto();
        dto2.setId(2L);

        Set<NotificationDto> dtos = new HashSet<>(Arrays.asList(dto1, dto2));
        when(notificationService.getUnreadNotifications(300L)).thenReturn(dtos);

        ResponseEntity<Set<NotificationDto>> response =
                notificationController.getUnreadNotifications(300L);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(dtos, response.getBody());
        verify(notificationService).getUnreadNotifications(300L);
    }

    @Test
    void testGetUnreadNotificationsEmptySet() {
        when(notificationService.getUnreadNotifications(400L)).thenReturn(Collections.emptySet());

        ResponseEntity<Set<NotificationDto>> response =
                notificationController.getUnreadNotifications(400L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(notificationService).getUnreadNotifications(400L);
    }
}
