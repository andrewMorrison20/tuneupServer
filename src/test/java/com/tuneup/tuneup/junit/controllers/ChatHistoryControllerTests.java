package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.chats.controllers.ChatHistoryController;
import com.tuneup.tuneup.chats.dtos.ConversationDto;
import com.tuneup.tuneup.chats.dtos.ConversationParticipantDto;
import com.tuneup.tuneup.chats.dtos.ConversationRequestDto;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import com.tuneup.tuneup.chats.services.ChatService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatHistoryControllerTests {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatHistoryController controller;

    private ConversationRequestDto requestDto;
    private ConversationDto conversationDto;
    private ConversationParticipantDto participant1;
    private ConversationParticipantDto participant2;
    private ProfileDto profileDto1;
    private ProfileDto profileDto2;
    private MessageDto messageDto1;
    private MessageDto messageDto2;

    @BeforeEach
    void setUp() {
        requestDto = new ConversationRequestDto();
        requestDto.setUserId(10L);
        requestDto.setParticipantId(20L);

        participant1 = new ConversationParticipantDto();
        participant1.setId(10L);
        participant1.setDisplayName("Alice");
        participant1.setProfilePictureUrl("url1");

        participant2 = new ConversationParticipantDto();
        participant2.setId(20L);
        participant2.setDisplayName("Bob");
        participant2.setProfilePictureUrl("url2");

        conversationDto = new ConversationDto();
        conversationDto.setId(100L);
        conversationDto.setParticipants(List.of(participant1, participant2));
        conversationDto.setLastMessage("Hello Bob");
        conversationDto.setLastMessageTimestamp(LocalDateTime.of(2025, 4, 1, 12, 30));

        profileDto1 = new ProfileDto();
        profileDto1.setId(1L);
        profileDto1.setDisplayName("Charlie");

        profileDto2 = new ProfileDto();
        profileDto2.setId(2L);
        profileDto2.setDisplayName("Dana");

        messageDto1 = new MessageDto();
        messageDto1.setId(1000L);
        messageDto2 = new MessageDto();
        messageDto2.setId(1001L);
    }

    @Test
    void startConversation_ReturnsConversationDto() {
        when(chatService.startConversation(10L, 20L)).thenReturn(conversationDto);

        var response = controller.startConversation(requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(conversationDto, response.getBody());
        verify(chatService).startConversation(10L, 20L);
    }

    @Test
    void getProfilesWithoutChatHistory_ReturnsPage() {
        int page = 1, size = 5;
        boolean active = false;
        Pageable pageable = PageRequest.of(page, size);

        List<ProfileDto> profilesList = List.of(profileDto1, profileDto2);
        Page<ProfileDto> pageResult = new PageImpl<>(profilesList, pageable, profilesList.size());

        when(chatService.getProfilesWithoutChatHistory(30L, pageable, active))
                .thenReturn(pageResult);

        ResponseEntity<Page<ProfileDto>> response =
                controller.getProfilesWithoutChatHistory(30L, page, size, active);

        assertEquals(200, response.getStatusCodeValue());
        Page<ProfileDto> body = response.getBody();
        assertNotNull(body);

        assertEquals(2, body.getContent().size());
        assertIterableEquals(profilesList, body.getContent());

        assertEquals(page, body.getNumber());
        assertEquals(size, body.getSize());

        verify(chatService).getProfilesWithoutChatHistory(30L, pageable, active);
    }


    @Test
    void getUserConversations_ReturnsPage() {
        int page = 0, size = 3;
        Pageable pageable = PageRequest.of(page, size);
        ConversationDto c1 = new ConversationDto(); c1.setId(200L);
        ConversationDto c2 = new ConversationDto(); c2.setId(201L);
        Page<ConversationDto> pageResult = new PageImpl<>(List.of(c1, c2), pageable, 2);
        when(chatService.getProfileConversations(50L, pageable)).thenReturn(pageResult);

        var response = controller.getUserConversations(50L, page, size);

        assertEquals(200, response.getStatusCodeValue());
        Page<ConversationDto> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getTotalElements());
        assertTrue(body.getContent().containsAll(List.of(c1, c2)));
        verify(chatService).getProfileConversations(50L, pageable);
    }

    @Test
    void getMessages_ReturnsPageOfMessages() {
        Pageable pageable = PageRequest.of(2, 4);
        Page<MessageDto> pageResult = new PageImpl<>(List.of(messageDto1, messageDto2), pageable, 2);
        when(chatService.getConversationMessages(77L, pageable)).thenReturn(pageResult);

        Page<MessageDto> result = controller.getMessages(77L, pageable);

        assertSame(pageResult, result);
        verify(chatService).getConversationMessages(77L, pageable);
    }
}
