package com.tuneup.tuneup.chats;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat")  // Client sends messages here
    @SendTo("/topic/messages")  // Broadcast to all subscribers
    public Message handleMessage(MessageDto messageDto) {
        return chatService.sendMessage(
                messageDto.getSenderProfileId(),
                messageDto.getConversationId(),
                messageDto.getContent()
        );
    }

    @PostMapping("/conversation/start")
    public ResponseEntity<ConversationDto> startConversation(@RequestBody ConversationRequest request) {
        ConversationDto conversation = chatService.startConversation(request.getUserId(), request.getParticipantId());
        return ResponseEntity.ok(conversation);
    }
}
