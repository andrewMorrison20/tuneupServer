package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.entities.Notification;
import com.tuneup.tuneup.notifications.enums.NotificationType;
import com.tuneup.tuneup.notifications.mappers.NotificationMapper;
import com.tuneup.tuneup.notifications.repositories.NotificationRepository;
import com.tuneup.tuneup.notifications.services.EmailNotificationService;
import com.tuneup.tuneup.notifications.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private NotificationMapper notificationMapper;
    @InjectMocks
    private NotificationService notificationService;

    private Notification savedNotification;
    private NotificationDto savedDto;

    @BeforeEach
    void setUp() {
        savedNotification = new Notification();
        savedNotification.setId(10L);
        savedNotification.setUserId(42L);
        savedNotification.setType(NotificationType.NEW_CHAT);
        savedNotification.setMessage("Hello");
        savedNotification.setRead(false);
        savedNotification.setTimestamp(LocalDateTime.now());

        savedDto = new NotificationDto();
        savedDto.setId(10L);
        savedDto.setUserId(42L);
        savedDto.setType(NotificationType.NEW_PAYMENT);
        savedDto.setMessage("Hello");
        savedDto.setRead(false);
        savedDto.setTimestamp(savedNotification.getTimestamp());
    }

    @Test
    void testCreateAndSendNotificationViaReflection() throws Exception {
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(notificationMapper.toDto(savedNotification)).thenReturn(savedDto);

        Method method = NotificationService.class
                .getDeclaredMethod("createAndSendNotification", NotificationType.class, String.class, Long.class);
        method.setAccessible(true);
        NotificationDto result = (NotificationDto) method.invoke(
                notificationService, NotificationType.NEW_CHAT, "Hello", 42L);

        assertEquals(savedDto, result);
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toDto(savedNotification);
        verify(messagingTemplate).convertAndSendToUser(
                eq("42"), eq("/queue/notifications"), eq(savedDto));
        verify(emailNotificationService).sendEmailNotification(savedDto);
    }

    @Test
    void testHandleNotificationEventTriggersCreateAndSend() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);
        when(notificationMapper.toDto(savedNotification)).thenReturn(savedDto);

        NotificationEvent event = new NotificationEvent(this,42L,NotificationType.NEW_CHAT, "Hello");
        notificationService.handleNotificationEvent(event);

        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSendToUser("42", "/queue/notifications", savedDto);
        verify(emailNotificationService).sendEmailNotification(savedDto);
    }

    @Test
    void testGetUnreadNotificationsReturnsSet() {
        Notification n1 = new Notification();
        n1.setId(1L);
        n1.setUserId(100L);
        n1.setRead(false);
        Notification n2 = new Notification();
        n2.setId(2L);
        n2.setUserId(100L);
        n2.setRead(false);
        List<Notification> list = Arrays.asList(n1, n2);

        NotificationDto d1 = new NotificationDto();
        d1.setId(1L);
        NotificationDto d2 = new NotificationDto();
        d2.setId(2L);

        when(notificationRepository.findByUserIdAndReadFalse(100L)).thenReturn(list);
        when(notificationMapper.toDto(n1)).thenReturn(d1);
        when(notificationMapper.toDto(n2)).thenReturn(d2);

        Set<NotificationDto> result = notificationService.getUnreadNotifications(100L);

        assertEquals(new HashSet<>(Set.of(d1, d2)), result);
        verify(notificationRepository).findByUserIdAndReadFalse(100L);
        verify(notificationMapper).toDto(n1);
        verify(notificationMapper).toDto(n2);
    }

    @Test
    void testGetUnreadNotificationsEmpty() {
        when(notificationRepository.findByUserIdAndReadFalse(200L)).thenReturn(List.of());
        Set<NotificationDto> result = notificationService.getUnreadNotifications(200L);
        assertTrue(result.isEmpty());
        verify(notificationRepository).findByUserIdAndReadFalse(200L);
    }
}
