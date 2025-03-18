package com.tuneup.tuneup.notifications;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

   Notification toEntity(NotificationDto notificationDto);

   NotificationDto toDto (Notification notification);
}
