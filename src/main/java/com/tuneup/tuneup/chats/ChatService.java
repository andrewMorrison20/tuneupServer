package com.tuneup.tuneup.chats;

import com.tuneup.tuneup.profiles.Profile;
import org.springframework.stereotype.Service;

@Service
public class ChatService {


    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public ChatService(MessageRepository messageRepository, ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
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
}
