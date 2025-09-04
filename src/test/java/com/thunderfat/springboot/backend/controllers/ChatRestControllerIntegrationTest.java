package com.thunderfat.springboot.backend.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.service.IChatService;

/**
 * Integration tests for Chat REST controller.
 * Tests all chat operations including legacy and modern methods.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@WebMvcTest(ChatRestController.class)
@DisplayName("Chat Controller Integration Tests")
class ChatRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatDTO testChatDTO;
    private List<ChatDTO> testChatList;

    @BeforeEach
    void setUp() {
        // Setup test data
        testChatDTO = new ChatDTO();
        testChatDTO.setIdChat(1);
        testChatDTO.setPacienteId(1);
        testChatDTO.setNutricionistaId(1);

        testChatList = Arrays.asList(testChatDTO);
    }

    // ================================
    // LEGACY CRUD OPERATIONS TESTS
    // ================================

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should list all chats (legacy method)")
    void shouldListAllChats() throws Exception {
        // Given
        when(chatService.listar()).thenReturn(testChatList);

        // When & Then
        mockMvc.perform(get("/chat/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idChat").value(1));

        verify(chatService).listar();
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find chat by ID")
    void shouldFindChatById() throws Exception {
        // Given
        when(chatService.buscarPorId(1)).thenReturn(testChatDTO);

        // When & Then
        mockMvc.perform(get("/chat/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idChat").value(1))
                .andExpect(jsonPath("$.pacienteId").value(1));

        verify(chatService).buscarPorId(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should create new chat")
    void shouldCreateNewChat() throws Exception {
        // Given
        doNothing().when(chatService).insertar(any(ChatDTO.class));

        // When & Then
        mockMvc.perform(post("/chat/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testChatDTO)))
                .andExpect(status().isCreated());

        verify(chatService).insertar(any(ChatDTO.class));
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should delete chat")
    void shouldDeleteChat() throws Exception {
        // Given
        doNothing().when(chatService).eliminar(1);

        // When & Then
        mockMvc.perform(delete("/chat/eliminar/1"))
                .andExpect(status().isNoContent());

        verify(chatService).eliminar(1);
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    @DisplayName("Should find chat for patient")
    void shouldFindChatForPatient() throws Exception {
        // Given
        when(chatService.buscarPorPaciente(1)).thenReturn(testChatDTO);

        // When & Then
        mockMvc.perform(get("/chat/paciente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pacienteId").value(1));

        verify(chatService).buscarPorPaciente(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find chats for nutritionist")
    void shouldFindChatsForNutritionist() throws Exception {
        // Given
        when(chatService.buscarPorNutricionista(1)).thenReturn(testChatList);

        // When & Then
        mockMvc.perform(get("/chat/nutricionista/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nutricionistaId").value(1));

        verify(chatService).buscarPorNutricionista(1);
    }

    // ================================
    // MODERN OPERATIONS TESTS
    // ================================

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find all chats with pagination")
    void shouldFindAllChatsWithPagination() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChatDTO> chatPage = new PageImpl<>(testChatList, pageable, 1);
        when(chatService.findAll(any(Pageable.class))).thenReturn(chatPage);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/todos")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(chatService).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find chat by ID with Optional return")
    void shouldFindChatByIdOptional() throws Exception {
        // Given
        when(chatService.findById(1)).thenReturn(Optional.of(testChatDTO));

        // When & Then
        mockMvc.perform(get("/chat/api/v2/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idChat").value(1));

        verify(chatService).findById(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should return not found for non-existent chat")
    void shouldReturnNotFoundForNonExistentChat() throws Exception {
        // Given
        when(chatService.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/chat/api/v2/999"))
                .andExpect(status().isNotFound());

        verify(chatService).findById(999);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find conversation between patient and nutritionist")
    void shouldFindConversation() throws Exception {
        // Given
        when(chatService.findConversation(1, 1)).thenReturn(Optional.of(testChatDTO));

        // When & Then
        mockMvc.perform(get("/chat/api/v2/conversation")
                .param("pacienteId", "1")
                .param("nutricionistaId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pacienteId").value(1))
                .andExpect(jsonPath("$.nutricionistaId").value(1));

        verify(chatService).findConversation(1, 1);
    }

    // ================================
    // ANALYTICS TESTS
    // ================================

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should get chat count for nutritionist")
    void shouldGetChatCountForNutritionist() throws Exception {
        // Given
        when(chatService.countChatsByNutritionist(1)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/nutritionist/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(5));

        verify(chatService).countChatsByNutritionist(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should get active chat count for nutritionist")
    void shouldGetActiveChatCountForNutritionist() throws Exception {
        // Given
        when(chatService.countActiveChatsByNutritionist(1)).thenReturn(3L);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/nutritionist/1/active-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(3));

        verify(chatService).countActiveChatsByNutritionist(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should check if chat exists")
    void shouldCheckIfChatExists() throws Exception {
        // Given
        when(chatService.chatExists(1, 1)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/exists")
                .param("pacienteId", "1")
                .param("nutricionistaId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));

        verify(chatService).chatExists(1, 1);
    }

    // ================================
    // REAL-TIME FEATURES TESTS
    // ================================

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find chats with unread messages")
    void shouldFindChatsWithUnreadMessages() throws Exception {
        // Given
        when(chatService.findChatsWithUnreadMessages(1)).thenReturn(testChatList);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/user/1/unread"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idChat").value(1));

        verify(chatService).findChatsWithUnreadMessages(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should find chats needing response")
    void shouldFindChatsNeedingResponse() throws Exception {
        // Given
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        when(chatService.findChatsNeedingResponse(eq(1), any(LocalDateTime.class)))
                .thenReturn(testChatList);

        // When & Then
        mockMvc.perform(get("/chat/api/v2/nutritionist/1/needs-response")
                .param("hoursAgo", "24"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(chatService).findChatsNeedingResponse(eq(1), any(LocalDateTime.class));
    }

    // ================================
    // ERROR HANDLING TESTS
    // ================================

    @Test
    @DisplayName("Should require authentication")
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/chat/todos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INVALID_ROLE")
    @DisplayName("Should require proper authorization")
    void shouldRequireProperAuthorization() throws Exception {
        mockMvc.perform(get("/chat/todos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        // Given
        when(chatService.buscarPorId(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/chat/1"))
                .andExpect(status().isInternalServerError());

        verify(chatService).buscarPorId(1);
    }

    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("Should validate request parameters")
    void shouldValidateRequestParameters() throws Exception {
        // Test with invalid chat ID
        mockMvc.perform(get("/chat/invalid"))
                .andExpect(status().isBadRequest());
    }
}
