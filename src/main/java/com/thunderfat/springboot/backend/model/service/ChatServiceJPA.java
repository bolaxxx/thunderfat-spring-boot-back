package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.ChatRepository;
import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.dto.ChatUnreadCountDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.ChatMapper;
import com.thunderfat.springboot.backend.model.entity.Chat;

import lombok.extern.slf4j.Slf4j;

/**
 * Modern Spring Boot 2025 implementation of Chat service.
 * Provides comprehensive chat messaging functionality with real-time features.
 * 
 * Key Features:
 * - Complete CRUD operations with modern patterns
 * - Advanced caching for performance optimization
 * - Real-time communication support
 * - Analytics and reporting capabilities
 * - Notification system integration
 * - Backward compatibility with legacy methods
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service
@Transactional
@Slf4j
public class ChatServiceJPA implements IChatService {
	
	@Autowired
	private ChatRepository chatRepository;

	// ================================
	// LEGACY CRUD OPERATIONS
	// ================================

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'all_legacy'")
	@Deprecated(since = "3.5.4", forRemoval = true)
	public List<ChatDTO> listar() {
		log.debug("Listing all chats (legacy method)");
		return chatRepository.findAll().stream()
				.map(ChatMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'single_legacy_' + #idChat")
	public ChatDTO buscarPorId(int idChat) {
		log.debug("Finding chat by ID (legacy): {}", idChat);
		return chatRepository.findById(idChat)
				.map(ChatMapper.INSTANCE::toDto)
				.orElse(null);
	}

	@Override
	@Transactional
	@CacheEvict(value = {"chats", "chat-stats"}, allEntries = true)
	public void insertar(ChatDTO chatDTO) {
		log.info("Creating new chat conversation");
		chatRepository.save(ChatMapper.INSTANCE.toEntity(chatDTO));
		log.info("Successfully created chat conversation");
	}

	@Override
	@Transactional
	@CacheEvict(value = {"chats", "chat-stats"}, allEntries = true)
	public void eliminar(int idChat) {
		log.info("Deleting chat: {}", idChat);
		chatRepository.deleteById(idChat);
		log.info("Successfully deleted chat: {}", idChat);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'patient_legacy_' + #idPaciente")
	@Deprecated(since = "3.5.4", forRemoval = true)
	public ChatDTO buscarPorPaciente(int idPaciente) {
		log.debug("Finding chat for patient (legacy): {}", idPaciente);
		Chat chat = chatRepository.findByPacienteId(idPaciente);
		return chat != null ? ChatMapper.INSTANCE.toDto(chat) : null;
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'nutritionist_legacy_' + #idNutricionista")
	@Deprecated(since = "3.5.4", forRemoval = true)
	public List<ChatDTO> buscarPorNutricionista(int idNutricionista) {
		log.debug("Finding chats for nutritionist (legacy): {}", idNutricionista);
		return chatRepository.findByNutricionistaId(idNutricionista).stream()
				.map(ChatMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
	}

	// ================================
	// MODERN OPERATIONS (Spring Boot 2025)
	// ================================

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
	public Page<ChatDTO> findAll(Pageable pageable) {
		log.debug("Finding all chats with pagination: page {}, size {}", 
				pageable.getPageNumber(), pageable.getPageSize());
		return chatRepository.findAll(pageable)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'single_' + #idChat")
	public Optional<ChatDTO> findById(Integer idChat) {
		log.debug("Finding chat by ID: {}", idChat);
		return chatRepository.findById(idChat)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'patient_' + #pacienteId")
	public Optional<ChatDTO> findByPacienteId(Integer pacienteId) {
		log.debug("Finding chat for patient: {}", pacienteId);
		return chatRepository.findByPacienteId(pacienteId)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'nutritionist_' + #nutricionistaId + '_' + #pageable.pageNumber")
	public Page<ChatDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable) {
		log.debug("Finding chats for nutritionist: {} with pagination", nutricionistaId);
		return chatRepository.findByNutricionistaId(nutricionistaId, pageable)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'conversation_' + #pacienteId + '_' + #nutricionistaId")
	public Optional<ChatDTO> findConversation(Integer pacienteId, Integer nutricionistaId) {
		log.debug("Finding conversation between patient {} and nutritionist {}", 
				pacienteId, nutricionistaId);
		return chatRepository.findByPacienteIdAndNutricionistaId(pacienteId, nutricionistaId)
				.map(ChatMapper.INSTANCE::toDto);
	}

	// ================================
	// ADVANCED CHAT OPERATIONS
	// ================================

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'active_' + #nutricionistaId + '_' + #pageable.pageNumber")
	public Page<ChatDTO> findActiveChatsForNutritionist(Integer nutricionistaId, Pageable pageable) {
		log.debug("Finding active chats for nutritionist: {}", nutricionistaId);
		return chatRepository.findActiveChatsForNutritionist(nutricionistaId, pageable)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'recent_activity_' + #userId + '_' + #since")
	public List<ChatDTO> findChatsWithRecentActivity(Integer userId, LocalDateTime since) {
		log.debug("Finding chats with recent activity for user: {} since: {}", userId, since);
		return chatRepository.findChatsWithRecentActivity(userId, since).stream()
				.map(ChatMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'unread_' + #userId")
	public List<ChatDTO> findChatsWithUnreadMessages(Integer userId) {
		log.debug("Finding chats with unread messages for user: {}", userId);
		return chatRepository.findChatsWithUnreadMessages(userId).stream()
				.map(ChatMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'needs_response_' + #nutricionistaId + '_' + #since")
	public List<ChatDTO> findChatsNeedingResponse(Integer nutricionistaId, LocalDateTime since) {
		log.debug("Finding chats needing response for nutritionist: {} since: {}", 
				nutricionistaId, since);
		return chatRepository.findChatsNeedingResponse(nutricionistaId, since).stream()
				.map(ChatMapper.INSTANCE::toDto)
				.collect(Collectors.toList());
	}

	// ================================
	// CHAT ANALYTICS
	// ================================

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chat-stats", key = "'count_nutritionist_' + #nutricionistaId")
	public Long countChatsByNutritionist(Integer nutricionistaId) {
		log.debug("Counting chats for nutritionist: {}", nutricionistaId);
		return chatRepository.countChatsByNutricionistaId(nutricionistaId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chat-stats", key = "'active_count_' + #nutricionistaId")
	public Long countActiveChatsByNutritionist(Integer nutricionistaId) {
		log.debug("Counting active chats for nutritionist: {}", nutricionistaId);
		return chatRepository.countActiveChatsByNutricionistaId(nutricionistaId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chat-stats", key = "'message_count_' + #chatId")
	public Long getMessageCount(Integer chatId) {
		log.debug("Getting message count for chat: {}", chatId);
		return chatRepository.countMessagesByChatId(chatId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'most_active_' + #nutricionistaId + '_' + #pageable.pageNumber")
	public Page<ChatDTO> findMostActiveChats(Integer nutricionistaId, Pageable pageable) {
		log.debug("Finding most active chats for nutritionist: {}", nutricionistaId);
		return chatRepository.findMostActiveChatsForNutritionist(nutricionistaId, pageable)
				.map(ChatMapper.INSTANCE::toDto);
	}

	// ================================
	// COMMUNICATION UTILITIES
	// ================================

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chat-stats", key = "'exists_' + #pacienteId + '_' + #nutricionistaId")
	public Boolean chatExists(Integer pacienteId, Integer nutricionistaId) {
		log.debug("Checking if chat exists between patient {} and nutritionist {}", 
				pacienteId, nutricionistaId);
		return chatRepository.existsByPacienteIdAndNutricionistaId(pacienteId, nutricionistaId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chats", key = "'history_' + #pacienteId + '_' + #pageable.pageNumber")
	public Page<ChatDTO> getChatHistory(Integer pacienteId, Pageable pageable) {
		log.debug("Getting chat history for patient: {}", pacienteId);
		return chatRepository.findChatHistoryForPatient(pacienteId, pageable)
				.map(ChatMapper.INSTANCE::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "chat-stats", key = "'unread_counts_' + #nutricionistaId")
	public List<ChatUnreadCountDTO> getChatsWithUnreadCounts(Integer nutricionistaId) {
		log.debug("Getting chats with unread counts for nutritionist: {}", nutricionistaId);
		return chatRepository.findChatsWithUnreadCount(nutricionistaId);
	}
}
