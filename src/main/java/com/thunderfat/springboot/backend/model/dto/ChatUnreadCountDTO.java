package com.thunderfat.springboot.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for chat unread message count data transfer.
 * Used in queries to aggregate unread message counts per chat.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUnreadCountDTO {
    
    /**
     * Chat ID
     */
    private Integer chatId;
    
    /**
     * Number of unread messages in the chat
     */
    private Long unreadCount;
}
