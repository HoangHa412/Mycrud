package org.example.mycrud.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.mycrud.model.ChatNotification;
import org.example.mycrud.entity.ChatMessage;
import org.example.mycrud.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;


    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload ChatMessage chatMessage){
        log.info("Message received: {}", chatMessage);
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId){
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}

