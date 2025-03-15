package com.tuneup.tuneup.chats;

import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/chats")
public class ChatHistoryController {

    private final ChatService chatService;

    public ChatHistoryController(ChatService chatService) {
        this.chatService = chatService;
    }


    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long senderProfileId,
            @RequestParam Long receiverProfileId,
            @RequestParam String content) {
        return ResponseEntity.ok(chatService.sendMessage(senderProfileId, receiverProfileId, content));
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


}

