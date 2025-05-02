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
    /**
     * Sends a message for a given conversation thread
     * @param conversationId Id of the thread to subscribe to and send a message on
     * @param messageDto the message to send
     * @return the message successfully sent else throw
     */
    @MessageMapping("/chat/send/{conversationId}")
    @SendTo("/topic/chat/{conversationId}")
    public MessageDto sendMessage(@DestinationVariable Long conversationId, MessageDto messageDto) {

               MessageDto newMessageDto =  chatService.sendMessage(messageDto);
               return newMessageDto;
    }

}
