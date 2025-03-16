package com.tuneup.tuneup.chats.services;

import com.tuneup.tuneup.chats.entities.Conversation;
import com.tuneup.tuneup.chats.mappers.ConversationMapper;
import com.tuneup.tuneup.chats.entities.Message;
import com.tuneup.tuneup.chats.mappers.MessageMapper;
import com.tuneup.tuneup.chats.dtos.ConversationDto;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import com.tuneup.tuneup.chats.repositories.ConversationRepository;
import com.tuneup.tuneup.chats.repositories.MessageRepository;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
public class ChatService {


    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ProfileService profileService;
    private final TuitionService tuitionService;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    public ChatService(MessageRepository messageRepository, ConversationRepository conversationRepository, ProfileService profileService, TuitionService tuitionService, ConversationMapper conversationMapper, MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.profileService = profileService;
        this.tuitionService = tuitionService;
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
    }

    /**
     * Creates a new message for a given conversartion or else throws.
     * @param messageDto details of the message to create
     * @return the new message as a dto
     */
    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {

        Profile sender = profileService.fetchProfileEntityInternal(messageDto.getSenderProfileId());

        Conversation conversation = conversationRepository.findById(messageDto.getConversationId())
                .orElseThrow(() -> new ValidationException("Conversation not found"));

        Message message = messageMapper.toEntity(messageDto);
        message.setConversation(conversation);
        message.setSenderProfile(sender);
        message = messageRepository.save(message);

        MessageDto newMessage =  messageMapper.toDto(message);
        newMessage.setSenderName(message.getSenderProfile().getDisplayName());
        newMessage.setSenderProfilePictureUrl(sender.getProfilePicture().getUrl());

        return newMessage;
    }

    /**
     * Gets a page of profiles that do not have a chat histroy with a given profile.
     * @param profileId id of the profile to fetch chat hitory for
     * @param pageable page to return
     * @param active if tuition is active or not
     * @return a page of profile dtos
     */
    public Page<ProfileDto> getProfilesWithoutChatHistory(Long profileId, Pageable pageable, boolean active) {

        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        boolean isTutor = profile.getProfileType() == ProfileType.TUTOR;

        return profileService.getProfilesWithoutChatHistory(profileId, isTutor, active, pageable);
        }


    /**
     * Gets a page of conversations given profile.
     * @param profileId id of the profile to fetch chat history for
     * @param pageable page to return
     * @param active if tuition is active or not
     * @return a page of profile dtos
     */
    public Page<ProfileDto> getProfileChatHistory(Long profileId, Pageable pageable, boolean active) {

        Profile profile = profileService.fetchProfileEntityInternal(profileId);
        boolean isTutor = profile.getProfileType() == ProfileType.TUTOR;

        return profileService.getProfilesWithoutChatHistory(profileId, isTutor, active, pageable);
    }

    /**
     * Creates a new conversation for two users, checks if one preexisting
     * @param userId id of the user starting the conversation
     * @param participantId id of the user receving initial message
     * @return conversationDto of existing or new conversation
     */
    public ConversationDto startConversation(Long userId, Long participantId) {
        Profile user = profileService.fetchProfileEntityInternal(userId);
        Profile participant = profileService.fetchProfileEntityInternal(participantId);

        // Check if a conversation already exists
        Optional<Conversation> existingConversation = conversationRepository.findByProfiles(user.getId(), participant.getId());
        if (existingConversation.isPresent()) {
            return conversationMapper.toDto(existingConversation.get());
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setProfile1(user);
        conversation.setProfile2(participant);
        Conversation savedConversation = conversationRepository.save(conversation);

        return  conversationMapper.toDto(savedConversation);
    }

    public Page<ConversationDto> getProfileConversations(Long profileId, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.findByProfileId(profileId, pageable);
        return conversations.map(conversationMapper::toDto);
    }
}
