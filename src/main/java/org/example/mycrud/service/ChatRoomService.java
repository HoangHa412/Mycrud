package org.example.mycrud.service;

import org.example.mycrud.entity.ChatRoom;
import org.example.mycrud.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId.toString());
                    }
                    return Optional.empty();
                });
    }

    private Object createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s-%s", senderId, recipientId);

        ChatRoom senderRecipients = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientRecipients = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepository.save(senderRecipients);
        chatRoomRepository.save(recipientRecipients);

        return chatId;
    }

}
