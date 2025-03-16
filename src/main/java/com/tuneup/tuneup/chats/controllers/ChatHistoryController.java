package com.tuneup.tuneup.chats.controllers;

import com.tuneup.tuneup.chats.dtos.ConversationDto;
import com.tuneup.tuneup.chats.dtos.ConversationRequestDto;
import com.tuneup.tuneup.chats.services.ChatService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
public class ChatHistoryController {

    private final ChatService chatService;

    public ChatHistoryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversation/start")
    public ResponseEntity<ConversationDto> startConversation(@RequestBody ConversationRequestDto request) {
        ConversationDto conversation = chatService.startConversation(request.getUserId(), request.getParticipantId());
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/noHistory/{profileId}")
    public ResponseEntity<Page<ProfileDto>> getProfilesWithoutChatHistory(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean active) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileDto> profiles = chatService.getProfilesWithoutChatHistory(profileId, pageable, active);

        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/conversations/{profileId}")
    public ResponseEntity<Page<ConversationDto>> getUserConversations(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ConversationDto> conversations = chatService.getProfileConversations(profileId, pageable);

        return ResponseEntity.ok(conversations);
    }

}

