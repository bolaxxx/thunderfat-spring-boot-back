package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.dto.ChatUnreadCountDTO;
import com.thunderfat.springboot.backend.model.entity.Chat;

/**
 * Modern Spring Boot 2025 repository for Chat entity.
 * Provides comprehensive chat messaging operations between patients and nutritionists.
 * 
 * Key Features:
 * - BaseRepository inheritance for standardized operations
 * - Advanced pagination and sorting capabilities
 * - Caching for improved performance
 * - Named parameter queries for better maintainability
 * - Business-specific query methods
 * - Communication analytics and reporting
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface ChatRepository extends BaseRepository<Chat, Integer> {

    // ================================
    // BASIC CHAT OPERATIONS
    // ================================

    /**
     * Find chat conversation between specific patient and nutritionist.
     * Cached for performance optimization.
     */
    @Query("SELECT c FROM Chat c WHERE c.paciente.id = :pacienteId")
    @Cacheable(value = "chats", key = "'patient_' + #pacienteId")
    Optional<Chat> findByPacienteId(@Param("pacienteId") Integer pacienteId);

    /**
     * Find all chat conversations for a specific nutritionist.
     * Supports pagination for large datasets.
     */
    @Query("SELECT c FROM Chat c WHERE c.nutricionista.id = :nutricionistaId")
    @Cacheable(value = "chats", key = "'nutritionist_' + #nutricionistaId + '_' + #pageable.pageNumber")
    Page<Chat> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);

    /**
     * Legacy method for backward compatibility.
     * @deprecated Use findByNutricionistaId(Integer, Pageable) instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    @Query("SELECT c FROM Chat c WHERE c.nutricionista.id = :nutricionistaId")
    List<Chat> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Find chat conversation between specific patient and nutritionist.
     */
    @Query("SELECT c FROM Chat c WHERE c.paciente.id = :pacienteId AND c.nutricionista.id = :nutricionistaId")
    @Cacheable(value = "chats", key = "'conversation_' + #pacienteId + '_' + #nutricionistaId")
    Optional<Chat> findByPacienteIdAndNutricionistaId(@Param("pacienteId") Integer pacienteId, 
                                                     @Param("nutricionistaId") Integer nutricionistaId);

    // ================================
    // ADVANCED CHAT QUERIES
    // ================================

    /**
     * Find all active chats for a nutritionist with recent activity.
     */
    @Query("SELECT c FROM Chat c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND EXISTS (SELECT m FROM Mensaje m WHERE m.chat.id = c.id)")
    @Cacheable(value = "chats", key = "'active_' + #nutricionistaId + '_' + #pageable.pageNumber")
    Page<Chat> findActiveChatsForNutritionist(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);

    /**
     * Find chats with recent messages within specified time period.
     */
    @Query("SELECT DISTINCT c FROM Chat c JOIN c.mensajes m " +
           "WHERE m.timestamp >= :since AND " +
           "(c.paciente.id = :userId OR c.nutricionista.id = :userId)")
    @Cacheable(value = "chats", key = "'recent_' + #userId + '_' + #since")
    List<Chat> findChatsWithRecentActivity(@Param("userId") Integer userId, 
                                          @Param("since") LocalDateTime since);

    /**
     * Find chats with unread messages for a specific user.
     */
    @Query("SELECT DISTINCT c FROM Chat c JOIN c.mensajes m " +
           "WHERE m.leido = false AND " +
           "((c.paciente.id = :userId AND m.emisor.id != :userId) OR " +
           " (c.nutricionista.id = :userId AND m.emisor.id != :userId))")
    @Cacheable(value = "chats", key = "'unread_' + #userId")
    List<Chat> findChatsWithUnreadMessages(@Param("userId") Integer userId);

    // ================================
    // CHAT ANALYTICS & REPORTING
    // ================================

    /**
     * Count total number of chats for a nutritionist.
     */
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.nutricionista.id = :nutricionistaId")
    @Cacheable(value = "chat-stats", key = "'count_nutritionist_' + #nutricionistaId")
    Long countChatsByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Count active chats (with messages) for a nutritionist.
     */
    @Query("SELECT COUNT(DISTINCT c) FROM Chat c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND EXISTS (SELECT m FROM Mensaje m WHERE m.chat.id = c.id)")
    @Cacheable(value = "chat-stats", key = "'active_count_' + #nutricionistaId")
    Long countActiveChatsByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Get total message count for a specific chat.
     */
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.chat.id = :chatId")
    @Cacheable(value = "chat-stats", key = "'message_count_' + #chatId")
    Long countMessagesByChatId(@Param("chatId") Integer chatId);

    /**
     * Find most active chats by message count for a nutritionist.
     */
    @Query("SELECT c FROM Chat c WHERE c.nutricionista.id = :nutricionistaId " +
           "ORDER BY (SELECT COUNT(m) FROM Mensaje m WHERE m.chat.id = c.id) DESC")
    Page<Chat> findMostActiveChatsForNutritionist(@Param("nutricionistaId") Integer nutricionistaId, 
                                                 Pageable pageable);

    // ================================
    // COMMUNICATION PATTERNS
    // ================================

    /**
     * Find chats that need nutritionist attention (recent patient messages without response).
     */
    @Query("""
        SELECT c FROM Chat c
        WHERE c.nutricionista.id = :nutricionistaId
          AND EXISTS (
            SELECT m FROM Mensaje m
            WHERE m.chat.id = c.id
              AND m.emisor.id = c.paciente.id
              AND m.timestamp = (
                SELECT MAX(m2.timestamp) FROM Mensaje m2 WHERE m2.chat.id = c.id
              )
              AND m.timestamp >= :since
          )
    """)
    List<Chat> findChatsNeedingResponse(
        @Param("nutricionistaId") Integer nutricionistaId,
        @Param("since") LocalDateTime since
    );
    /**
     * Find patient's chat history with any nutritionist.
     */
    @Query("SELECT c FROM Chat c WHERE c.paciente.id = :pacienteId ORDER BY c.id DESC")
    Page<Chat> findChatHistoryForPatient(@Param("pacienteId") Integer pacienteId, Pageable pageable);

    /**
     * Check if a chat exists between patient and nutritionist.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chat c " +
           "WHERE c.paciente.id = :pacienteId AND c.nutricionista.id = :nutricionistaId")
    Boolean existsByPacienteIdAndNutricionistaId(@Param("pacienteId") Integer pacienteId, 
                                                @Param("nutricionistaId") Integer nutricionistaId);

    // ================================
    // NOTIFICATION SUPPORT
    // ================================

    /**
     * Find chats with unread messages count for dashboard notifications.
     */
    @Query("SELECT new com.thunderfat.springboot.backend.model.dto.ChatUnreadCountDTO(c.id, COUNT(m)) FROM Chat c LEFT JOIN c.mensajes m " +
           "WHERE c.nutricionista.id = :nutricionistaId AND m.leido = false " +
           "AND m.emisor.id = c.paciente.id GROUP BY c.id")
    List<ChatUnreadCountDTO> findChatsWithUnreadCount(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Get last message timestamp for each chat of a nutritionist.
     */
    @Query("SELECT c, MAX(m.timestamp) FROM Chat c LEFT JOIN c.mensajes m " +
           "WHERE c.nutricionista.id = :nutricionistaId GROUP BY c ORDER BY MAX(m.timestamp) DESC")
    Page<Object[]> findChatsWithLastMessageTime(@Param("nutricionistaId") Integer nutricionistaId, 
                                               Pageable pageable);

    // ================================
    // LEGACY SUPPORT (Deprecated)
    // ================================

    /**
     * Legacy method with positional parameter.
     * @deprecated Use findByPacienteId(Integer) instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    @Query("SELECT c FROM Chat c WHERE c.paciente.id = ?1")
    Chat findByPacienteId(int id_paciente);

    /**
     * Legacy method with positional parameter.
     * @deprecated Use findByNutricionistaId(Integer, Pageable) instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    @Query("SELECT c FROM Chat c WHERE c.nutricionista.id = ?1")
    List<Chat> findByNutricionistaId(int id_nutricionista);
}
