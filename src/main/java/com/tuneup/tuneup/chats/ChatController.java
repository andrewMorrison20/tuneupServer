package com.tuneup.tuneup.chats;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long senderProfileId,
            @RequestParam Long receiverProfileId,
            @RequestParam String content) {
        return ResponseEntity.ok(chatService.sendMessage(senderProfileId, receiverProfileId, content));
    }
}

