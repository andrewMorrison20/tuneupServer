package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.chats.entities.Conversation;
import com.tuneup.tuneup.chats.entities.Message;
import com.tuneup.tuneup.chats.dtos.ConversationDto;
import com.tuneup.tuneup.chats.dtos.MessageDto;
import com.tuneup.tuneup.chats.mappers.ConversationMapper;
import com.tuneup.tuneup.chats.mappers.MessageMapper;
import com.tuneup.tuneup.chats.repositories.ConversationRepository;
import com.tuneup.tuneup.chats.repositories.MessageRepository;
import com.tuneup.tuneup.chats.services.ChatService;
import com.tuneup.tuneup.images.entities.Image;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.enums.NotificationType;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.model.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatServiceTests {

    @Mock MessageRepository messageRepo;
    @Mock ConversationRepository convRepo;
    @Mock ProfileService profileService;
    @Mock ConversationMapper convMapper;
    @Mock MessageMapper msgMapper;
    @Mock ApplicationEventPublisher publisher;
    @InjectMocks ChatService chatService;

    @Test
    void sendMessage_Success() {
        // Input DTO
        MessageDto inDto = new MessageDto();
        inDto.setConversationId(2L);
        inDto.setSenderProfileId(1L);
        inDto.setContent("hello");

        // Prepare sender profile with real Image entity
        Profile sender = new Profile();
        sender.setId(1L);
        sender.setDisplayName("Alice");
        Image img = new Image();
        img.setUrl("pic.png");
        sender.setProfilePicture(img);
        AppUser senderUser = new AppUser();
        senderUser.setId(10L);
        sender.setAppUser(senderUser);

        // The other participant
        Profile other = new Profile();
        other.setId(2L);
        AppUser otherUser = new AppUser();
        otherUser.setId(20L);
        other.setAppUser(otherUser);

        // Conversation entity
        Conversation conv = new Conversation();
        conv.setId(2L);
        conv.setProfile1(sender);
        conv.setProfile2(other);

        // Message entity and DTO
        Message msgEntity = new Message();
        msgEntity.setId(5L);
        msgEntity.setContent("hello");

        MessageDto outDto = new MessageDto();
        outDto.setId(5L);

        // Stub each collaborator
        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(sender);
        when(convRepo.findById(2L)).thenReturn(Optional.of(conv));
        when(msgMapper.toEntity(inDto)).thenReturn(msgEntity);
        when(messageRepo.save(msgEntity)).thenReturn(msgEntity);
        when(msgMapper.toDto(msgEntity)).thenReturn(outDto);
        when(convRepo.save(conv)).thenReturn(conv);

        // Call under test
        MessageDto result = chatService.sendMessage(inDto);

        // Verify result
        assertSame(outDto, result);
        assertEquals("Alice", result.getSenderName());
        assertEquals("pic.png", result.getSenderProfilePictureUrl());
        assertEquals(msgEntity, conv.getLastMessage());

        // Verify notification event
        ArgumentCaptor<NotificationEvent> cap = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(publisher).publishEvent(cap.capture());
        NotificationEvent ev = cap.getValue();
        assertEquals(20L, ev.getUserId());
        assertEquals(NotificationType.NEW_CHAT, ev.getNotificationType());
    }

    @Test
    void sendMessage_ConversationNotFound_Throws() {
        MessageDto in = new MessageDto();
        in.setConversationId(2L);
        in.setSenderProfileId(1L);

        Profile sender = new Profile();
        sender.setId(1L);

        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(sender);
        when(convRepo.findById(2L)).thenReturn(Optional.empty());

        ValidationException ex = assertThrows(ValidationException.class,
                () -> chatService.sendMessage(in));
        assertEquals("Conversation not found", ex.getMessage());
    }

    @Test
    void getProfilesWithoutChatHistory_Tutor() {
        Pageable pg = PageRequest.of(0,1);
        Profile tutor = new Profile();
        tutor.setProfileType(ProfileType.TUTOR);

        when(profileService.fetchProfileEntityInternal(3L)).thenReturn(tutor);
        Page<ProfileDto> page = new PageImpl<>(List.of(new ProfileDto()), pg,1);
        when(profileService.getProfilesWithoutChatHistory(3L, true, false, pg))
                .thenReturn(page);

        Page<ProfileDto> res = chatService.getProfilesWithoutChatHistory(3L, pg, false);
        assertSame(page, res);
    }

    @Test
    void getProfilesWithoutChatHistory_Student() {
        Pageable pg = PageRequest.of(0,1);
        Profile student = new Profile();
        student.setProfileType(ProfileType.STUDENT);

        when(profileService.fetchProfileEntityInternal(7L)).thenReturn(student);
        Page<ProfileDto> page = Page.empty(pg);
        when(profileService.getProfilesWithoutChatHistory(7L, false, true, pg))
                .thenReturn(page);

        Page<ProfileDto> res = chatService.getProfilesWithoutChatHistory(7L, pg, true);
        assertSame(page, res);
    }

    @Test
    void startConversation_Existing() {
        Profile u = new Profile(); u.setId(1L);
        Profile p = new Profile(); p.setId(2L);
        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(u);
        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(p);

        Conversation existing = new Conversation();
        existing.setId(30L);
        when(convRepo.findByProfiles(1L,2L)).thenReturn(Optional.of(existing));

        ConversationDto dto = new ConversationDto();
        when(convMapper.toDto(existing)).thenReturn(dto);

        ConversationDto res = chatService.startConversation(1L,2L);
        assertSame(dto, res);
        verify(convRepo, never()).save(any());
    }

    @Test
    void startConversation_New() {
        Profile u = new Profile(); u.setId(1L);
        Profile p = new Profile(); p.setId(2L);
        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(u);
        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(p);

        when(convRepo.findByProfiles(1L,2L)).thenReturn(Optional.empty());
        Conversation saved = new Conversation();
        saved.setId(99L);
        when(convRepo.save(any())).thenReturn(saved);

        ConversationDto dto = new ConversationDto();
        when(convMapper.toDto(saved)).thenReturn(dto);

        ConversationDto res = chatService.startConversation(1L,2L);
        assertSame(dto, res);
        verify(convRepo).save(any());
    }

    @Test
    void getProfileConversations() {
        Pageable pg = PageRequest.of(0,2);
        Conversation c = new Conversation();
        c.setId(40L);
        Page<Conversation> page = new PageImpl<>(List.of(c), pg,1);
        when(convRepo.findByProfileId(9L, pg)).thenReturn(page);

        ConversationDto cd = new ConversationDto();
        cd.setId(40L);
        when(convMapper.toDto(c)).thenReturn(cd);

        Page<ConversationDto> out = chatService.getProfileConversations(9L, pg);
        assertEquals(1, out.getTotalElements());
        assertEquals(40L, out.getContent().get(0).getId());
    }

    @Test
    void getConversationMessages_Reversed() {
        Pageable pg = PageRequest.of(0,2);
        Message m1 = new Message(); m1.setId(50L);
        Message m2 = new Message(); m2.setId(51L);
        Page<Message> page = new PageImpl<>(List.of(m1, m2), pg,2);
        when(messageRepo.findMessagesByConversationId(5L, pg)).thenReturn(page);

        MessageDto d1 = new MessageDto(); d1.setId(50L);
        MessageDto d2 = new MessageDto(); d2.setId(51L);
        when(msgMapper.toDto(m1)).thenReturn(d1);
        when(msgMapper.toDto(m2)).thenReturn(d2);

        Page<MessageDto> res = chatService.getConversationMessages(5L, pg);
        // should reverse order
        assertEquals(List.of(d2, d1), res.getContent());
    }
}
