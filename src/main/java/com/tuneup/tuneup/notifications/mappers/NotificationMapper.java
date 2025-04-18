package com.tuneup.tuneup.notifications.mappers;

import com.tuneup.tuneup.notifications.dtos.NotificationDto;
import com.tuneup.tuneup.notifications.entities.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

   Notification toEntity(NotificationDto notificationDto);

   NotificationDto toDto (Notification notification);
}
