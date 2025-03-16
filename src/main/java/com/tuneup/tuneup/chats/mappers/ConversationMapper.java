package com.tuneup.tuneup.chats.mappers;


import com.tuneup.tuneup.chats.entities.Conversation;
import com.tuneup.tuneup.chats.dtos.ConversationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )

public interface ConversationMapper {

    @Mapping(target = "participants", expression = "java(java.util.Arrays.asList(conversation.getProfile1().getDisplayName(), conversation.getProfile2().getDisplayName()))")
    @Mapping(target = "lastMessage", expression = "java(conversation.getLastMessage() != null ? conversation.getLastMessage().getContent() : null)")
    @Mapping(target = "lastMessageTimestamp", expression = "java(conversation.getLastMessage() != null ? conversation.getLastMessage().getTimestamp() : null)")
    ConversationDto toDto(Conversation conversation);

    @Mapping(target = "profile1", ignore = true)
    @Mapping(target = "profile2", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Conversation toEntity(ConversationDto conversationDto);
}
