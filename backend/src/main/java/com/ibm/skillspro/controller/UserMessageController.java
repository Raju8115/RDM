package com.ibm.skillspro.controller;

import com.ibm.skillspro.entity.UserMessage;
import com.ibm.skillspro.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user-messages")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserMessageController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserMessageController.class);

    @Autowired
    private UserMessageRepository userMessageRepository;

    @GetMapping("/{userEmail}")
    public ResponseEntity<List<UserMessage>> getUserMessages(@PathVariable String userEmail) {
        List<UserMessage> messages = userMessageRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(userEmail);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{userEmail}/unread")
    public ResponseEntity<List<UserMessage>> getUnreadMessages(@PathVariable String userEmail) {
        List<UserMessage> messages = userMessageRepository.findByUserEmailIgnoreCaseAndReadOrderByCreatedAtDesc(userEmail, false);
        logger.info("[USER MESSAGE DEBUG] Fetching unread messages for {}: count={}", userEmail, messages.size());
        for (UserMessage msg : messages) {
            logger.info("[USER MESSAGE DEBUG] Unread message: id={}, reason={}, read={}", msg.getId(), msg.getReason(), msg.getRead());
        }
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long messageId) {
        UserMessage message = userMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            logger.info("[USER MESSAGE DEBUG] Marking message as read: id={}, userEmail={}, reason={}", message.getId(), message.getUserEmail(), message.getReason());
            message.setRead(true);
            userMessageRepository.save(message);
            return ResponseEntity.ok().build();
        }
        logger.warn("[USER MESSAGE DEBUG] Tried to mark non-existent message as read: id={}", messageId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UserMessage> createMessage(@RequestBody UserMessage message) {
        if (message.getRead() == null) {
            message.setRead(false);
        }
        // Prevent duplicate rejection messages
        if ("REJECTION".equals(message.getMessageType())) {
            List<UserMessage> existing = userMessageRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(message.getUserEmail());
            for (UserMessage msg : existing) {
                if (!msg.getRead() && "REJECTION".equals(msg.getMessageType()) && msg.getReason() != null && msg.getReason().equals(message.getReason())) {
                    // Duplicate found, return existing message
                    return ResponseEntity.ok(msg);
                }
            }
        }
        UserMessage savedMessage = userMessageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/{userEmail}/count")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable String userEmail) {
        long count = userMessageRepository.countByUserEmailAndRead(userEmail, false);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{userEmail}/mark-all-unread")
    public ResponseEntity<?> markAllMessagesAsUnread(@PathVariable String userEmail) {
        List<UserMessage> messages = userMessageRepository.findByUserEmailIgnoreCaseOrderByCreatedAtDesc(userEmail);
        for (UserMessage msg : messages) {
            msg.setRead(false);
        }
        userMessageRepository.saveAll(messages);
        return ResponseEntity.ok().build();
    }
} 