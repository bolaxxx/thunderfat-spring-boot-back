package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.dto.ChatUnreadCountDTO;

/**
 * Modern Spring Boot 2025 service interface for Chat operations.
 * Provides comprehensive chat messaging functionality between patients and nutritionists.
 * 
 * Features:
 * - Legacy method support for backward compatibility
 * - Modern pagination and Optional returns
 * - Chat analytics and reporting
 * - Communication pattern analysis
 * - Notification support
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IChatService {

    // ================================
    // LEGACY CRUD OPERATIONS
    // ================================

    /**
     * List all chats in the system.
     * @deprecated Use findAll(Pageable) for better performance
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    List<ChatDTO> listar();

    /**
     * Find chat by ID.
     */
    ChatDTO buscarPorId(int idChat);

    /**
     * Create new chat.
     */
    void insertar(ChatDTO chatDTO);

    /**
     * Delete chat by ID.
     */
    void eliminar(int idChat);

    /**
     * Find chat for specific patient.
     * @deprecated Use findByPacienteId(Integer) instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    ChatDTO buscarPorPaciente(int idPaciente);

    /**
     * Find chats for specific nutritionist.
     * @deprecated Use findByNutricionistaId(Integer, Pageable) instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    List<ChatDTO> buscarPorNutricionista(int idNutricionista);

    // ================================
    // MODERN OPERATIONS (Spring Boot 2025)
    // ================================

    /**
     * Find all chats with pagination support.
     */
    Page<ChatDTO> findAll(Pageable pageable);

    /**
     * Find chat by ID with Optional return for null safety.
     */
    Optional<ChatDTO> findById(Integer idChat);

    /**
     * Find chat for specific patient with Optional return.
     */
    Optional<ChatDTO> findByPacienteId(Integer pacienteId);

    /**
     * Find chats for nutritionist with pagination.
     */
    Page<ChatDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable);

    /**
     * Find conversation between patient and nutritionist.
     */
    Optional<ChatDTO> findConversation(Integer pacienteId, Integer nutricionistaId);

    // ================================
    // ADVANCED CHAT OPERATIONS
    // ================================

    /**
     * Find active chats (with messages) for nutritionist.
     */
    Page<ChatDTO> findActiveChatsForNutritionist(Integer nutricionistaId, Pageable pageable);

    /**
     * Find chats with recent activity for user.
     */
    List<ChatDTO> findChatsWithRecentActivity(Integer userId, LocalDateTime since);

    /**
     * Find chats with unread messages for user.
     */
    List<ChatDTO> findChatsWithUnreadMessages(Integer userId);

    /**
     * Find chats that need response from nutritionist.
     */
    List<ChatDTO> findChatsNeedingResponse(Integer nutricionistaId, LocalDateTime since);

    // ================================
    // CHAT ANALYTICS
    // ================================

    /**
     * Count total chats for nutritionist.
     */
    Long countChatsByNutritionist(Integer nutricionistaId);

    /**
     * Count active chats for nutritionist.
     */
    Long countActiveChatsByNutritionist(Integer nutricionistaId);

    /**
     * Get message count for specific chat.
     */
    Long getMessageCount(Integer chatId);

    /**
     * Find most active chats by message volume.
     */
    Page<ChatDTO> findMostActiveChats(Integer nutricionistaId, Pageable pageable);

    // ================================
    // COMMUNICATION UTILITIES
    // ================================

    /**
     * Check if chat exists between patient and nutritionist.
     */
    Boolean chatExists(Integer pacienteId, Integer nutricionistaId);

    /**
     * Get patient's chat history.
     */
    Page<ChatDTO> getChatHistory(Integer pacienteId, Pageable pageable);

    /**
     * Get chats with unread message counts for notifications.
     */
    List<ChatUnreadCountDTO> getChatsWithUnreadCounts(Integer nutricionistaId);
}
