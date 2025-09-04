package com.thunderfat.springboot.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.service.IChatService;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket controller for real-time chat messaging.
 * Handles message broadcasting, private messaging, and notifications.
 * 
 * Features:
 * - Real-time message delivery
 * - Private conversation channels
 * - User presence notifications
 * - Message delivery confirmations
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Controller
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@Slf4j
public class ChatWebSocketController {

    @Autowired
    private IChatService chatService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handle new messages sent to a specific chat conversation.
     * Messages are broadcasted to all participants in the conversation.
     */
    @MessageMapping("/chat/{chatId}/message")
    @SendTo("/topic/chat/{chatId}")
    public ChatMessage handleChatMessage(@DestinationVariable String chatId, 
                                       @Payload ChatMessage message,
                                       Authentication authentication) {
        log.info("Received message for chat {}: {}", chatId, message.getContent());
        
        // Add timestamp and sender information
        message.setTimestamp(LocalDateTime.now());
        message.setSender(authentication.getName());
        message.setChatId(chatId);
        
        // You could save the message to database here
        // messageService.saveMessage(message);
        
        return message;
    }

    /**
     * Handle private messages between users.
     * Messages are sent directly to the recipient's queue.
     */
    @MessageMapping("/chat/private")
    public void handlePrivateMessage(@Payload PrivateMessage message, 
                                   Authentication authentication) {
        log.info("Sending private message from {} to {}", 
                authentication.getName(), message.getRecipient());
        
        message.setTimestamp(LocalDateTime.now());
        message.setSender(authentication.getName());
        
        // Send to recipient's private queue
        messagingTemplate.convertAndSendToUser(
            message.getRecipient(), 
            "/queue/messages", 
            message
        );
        
        // Optional: Send delivery confirmation to sender
        messagingTemplate.convertAndSendToUser(
            authentication.getName(),
            "/queue/confirmations",
            new MessageConfirmation(message.getId(), "DELIVERED")
        );
    }

    /**
     * Handle user presence notifications (user joined/left chat).
     */
    @MessageMapping("/chat/{chatId}/join")
    @SendTo("/topic/chat/{chatId}/presence")
    public PresenceNotification handleUserJoin(@DestinationVariable String chatId,
                                             Authentication authentication) {
        log.info("User {} joined chat {}", authentication.getName(), chatId);
        
        return new PresenceNotification(
            authentication.getName(),
            "JOINED",
            LocalDateTime.now(),
            chatId
        );
    }

    /**
     * Handle user leaving chat.
     */
    @MessageMapping("/chat/{chatId}/leave")
    @SendTo("/topic/chat/{chatId}/presence")
    public PresenceNotification handleUserLeave(@DestinationVariable String chatId,
                                              Authentication authentication) {
        log.info("User {} left chat {}", authentication.getName(), chatId);
        
        return new PresenceNotification(
            authentication.getName(),
            "LEFT",
            LocalDateTime.now(),
            chatId
        );
    }

    /**
     * Handle typing indicators.
     */
    @MessageMapping("/chat/{chatId}/typing")
    @SendTo("/topic/chat/{chatId}/typing")
    public TypingIndicator handleTypingIndicator(@DestinationVariable String chatId,
                                               @Payload TypingIndicator indicator,
                                               Authentication authentication) {
        indicator.setUser(authentication.getName());
        indicator.setChatId(chatId);
        indicator.setTimestamp(LocalDateTime.now());
        
        return indicator;
    }

    /**
     * Get unread message notifications for a user.
     */
    @MessageMapping("/notifications/unread")
    @SendToUser("/queue/notifications")
    public List<ChatDTO> getUnreadNotifications(Authentication authentication) {
        log.info("Getting unread notifications for user: {}", authentication.getName());
        
        // Extract user ID from authentication
        // This would need to be implemented based on your auth system
        Integer userId = extractUserIdFromAuth(authentication);
        
        return chatService.findChatsWithUnreadMessages(userId);
    }

    /**
     * Broadcast notification to all nutritionists about new patient message.
     */
    public void broadcastNewMessageNotification(String nutricionistaId, ChatMessage message) {
        NotificationMessage notification = new NotificationMessage(
            "NEW_MESSAGE",
            "New message from patient",
            message,
            LocalDateTime.now()
        );
        
        messagingTemplate.convertAndSendToUser(
            nutricionistaId,
            "/queue/notifications",
            notification
        );
    }

    /**
     * Send real-time chat updates to users.
     */
    public void sendChatUpdate(String chatId, String updateType, Object data) {
        ChatUpdate update = new ChatUpdate(updateType, data, LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/updates", update);
    }

    // Helper method to extract user ID from authentication
    private Integer extractUserIdFromAuth(Authentication authentication) {
        // Implementation depends on your authentication system
        // This is a placeholder
        return 1; // Replace with actual implementation
    }

    // DTO classes for WebSocket messages
    public static class ChatMessage {
        private String id;
        private String content;
        private String sender;
        private String chatId;
        private LocalDateTime timestamp;
        private String messageType = "TEXT";

        // Constructors, getters, setters
        public ChatMessage() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
    }

    public static class PrivateMessage {
        private String id;
        private String content;
        private String sender;
        private String recipient;
        private LocalDateTime timestamp;

        // Constructors, getters, setters
        public PrivateMessage() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class PresenceNotification {
        private String user;
        private String status; // JOINED, LEFT, ONLINE, OFFLINE
        private LocalDateTime timestamp;
        private String chatId;

        public PresenceNotification(String user, String status, LocalDateTime timestamp, String chatId) {
            this.user = user;
            this.status = status;
            this.timestamp = timestamp;
            this.chatId = chatId;
        }

        // Getters and setters
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
    }

    public static class TypingIndicator {
        private String user;
        private String chatId;
        private boolean isTyping;
        private LocalDateTime timestamp;

        // Constructors, getters, setters
        public TypingIndicator() {}

        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public boolean isTyping() { return isTyping; }
        public void setTyping(boolean typing) { isTyping = typing; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class MessageConfirmation {
        private String messageId;
        private String status; // SENT, DELIVERED, READ

        public MessageConfirmation(String messageId, String status) {
            this.messageId = messageId;
            this.status = status;
        }

        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class NotificationMessage {
        private String type;
        private String message;
        private Object data;
        private LocalDateTime timestamp;

        public NotificationMessage(String type, String message, Object data, LocalDateTime timestamp) {
            this.type = type;
            this.message = message;
            this.data = data;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class ChatUpdate {
        private String type;
        private Object data;
        private LocalDateTime timestamp;

        public ChatUpdate(String type, Object data, LocalDateTime timestamp) {
            this.type = type;
            this.data = data;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
