package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.chats.controllers.ChatController;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import com.tuneup.tuneup.chats.services.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatControllerTests {

    private ChatService chatService;
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        chatService = mock(ChatService.class);
        chatController = new ChatController(chatService);
    }

    @Test
    void sendMessage_ShouldDelegateToServiceAndReturnResult() {
        Long conversationId = 123L;
        MessageDto input = new MessageDto();
        input.setSenderProfileId(1L);
        input.setConversationId(conversationId);
        input.setContent("Hello!");

        MessageDto expected = new MessageDto();
        expected.setSenderProfileId(1L);
        expected.setConversationId(conversationId);
        expected.setContent("Hello!");

        when(chatService.sendMessage(input)).thenReturn(expected);

        MessageDto result = chatController.sendMessage(conversationId, input);

        assertSame(expected, result);
        verify(chatService, times(1)).sendMessage(input);
    }

    @Test
    void sendMessage_ShouldHaveCorrectAnnotations() throws NoSuchMethodException {
        Method m = ChatController.class.getMethod("sendMessage", Long.class, MessageDto.class);

        MessageMapping mm = m.getAnnotation(MessageMapping.class);
        assertNotNull(mm);
        String[] mappingValues = mm.value();
        assertEquals(1, mappingValues.length);
        assertEquals("/chat/send/{conversationId}", mappingValues[0]);

        SendTo st = m.getAnnotation(SendTo.class);
        assertNotNull(st);
        String[] toValues = st.value();
        assertEquals(1, toValues.length);
        assertEquals("/topic/chat/{conversationId}", toValues[0]);

        // verify parameter annotation
        DestinationVariable dv = m.getParameters()[0].getAnnotation(DestinationVariable.class);
        assertNotNull(dv);
    }
}
