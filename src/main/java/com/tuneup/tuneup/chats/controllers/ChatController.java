package com.tuneup.tuneup.chats.controllers;

import com.tuneup.tuneup.chats.services.ChatService;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    //Websockets dont use response entity
    @MessageMapping("/chat/{conversationId}")
    @SendTo("/topic/chat/{conversationId}")
    public MessageDto sendMessage(@DestinationVariable Long conversationId, MessageDto messageDto) {
        return chatService.sendMessage(messageDto);
    }

}
