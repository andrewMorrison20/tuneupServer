package com.tuneup.tuneup.chats;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.Tuition;
import com.tuneup.tuneup.tuitions.TuitionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class ChatService {


    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ProfileService profileService;
    private final TuitionService tuitionService;

    public ChatService(MessageRepository messageRepository, ConversationRepository conversationRepository, ProfileService profileService, TuitionService tuitionService) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.profileService = profileService;
        this.tuitionService = tuitionService;
    }

    public Message sendMessage(Long senderProfileId, Long receiverProfileId, String content) {

        Profile sender = new Profile();
        sender.setId(senderProfileId);
        Profile receiver = new Profile();
        receiver.setId(receiverProfileId);

        // Find or create conversation
        Conversation conversation = conversationRepository
                .findByProfiles(sender, receiver)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setProfile1(sender);
                    newConv.setProfile2(receiver);
                    return conversationRepository.save(newConv);
                });

        // Save message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderProfile(sender);
        message.setContent(content);
        return messageRepository.save(message);
    }

    public Page<ProfileDto> getProfilesWithoutChatHistory(Long profileId, Pageable pageable, boolean active) {

        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        boolean isTutor = profile.getProfileType() == ProfileType.TUTOR;

        return profileService.getProfilesWithoutChatHistory(profileId, isTutor, active, pageable);
        }


    public ConversationDto startConversation(Long userId, Long participantId) {
        Profile user = profileService.fetchProfileEntityInternal(userId);
        Profile participant = profileService.fetchProfileEntityInternal(participantId);

        // Check if a conversation already exists
        Optional<Conversation> existingConversation = conversationRepository.findByProfiles(user, participant);
        if (existingConversation.isPresent()) {
            return new ConversationDto(existingConversation.get());
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setProfile1(user);
        conversation.setProfile2(participant);
        Conversation savedConversation = conversationRepository.save(conversation);

        return new ConversationDto(savedConversation);
    }
}
