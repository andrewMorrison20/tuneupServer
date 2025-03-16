package com.tuneup.tuneup.chats.mappers;

import com.tuneup.tuneup.chats.entities.Message;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface MessageMapper {


    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "senderProfile", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "read", constant = "false")
    Message toEntity(MessageDto messageDto);


    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderProfileId", source = "senderProfile.id")
    MessageDto toDto(Message message);
}

