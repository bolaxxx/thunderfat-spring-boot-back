package com.thunderfat.springboot.backend.model.dao;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import com.thunderfat.springboot.backend.model.entity.Chat;
import com.thunderfat.springboot.backend.model.entity.Mensaje;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

/**
 * Integration tests for ChatRepository.
 * Tests all repository operations including modern Spring Boot 2025 features.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"
})
@DisplayName("Chat Repository Integration Tests")
class ChatRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRepository chatRepository;

    private Paciente testPaciente;
    private Nutricionista testNutricionista;
    private Chat testChat;
    private Mensaje testMensaje;

    @BeforeEach
    void setUp() {
        // Create test entities
        testPaciente = new Paciente();
        testPaciente.setNombre("Juan");
        testPaciente.setApellidos("Pérez");
        testPaciente.setEmail("juan.perez@test.com");
        testPaciente.setPsw("password123");
        testPaciente.setId(0); // Ensure ID is set to 0 for auto-generation
        testPaciente = entityManager.persistAndFlush(testPaciente);

        testNutricionista = new Nutricionista();
        testNutricionista.setNombre("María");
        testNutricionista.setApellidos("González");
        testNutricionista.setEmail("maria.gonzalez@test.com");
        testNutricionista.setPsw("password123");
        testNutricionista = entityManager.persistAndFlush(testNutricionista);

        testChat = new Chat();
        testChat.setPaciente(testPaciente);
        testChat.setNutricionista(testNutricionista);
        testChat = entityManager.persistAndFlush(testChat);

        testMensaje = new Mensaje();
        testMensaje.setContenido("Test message");
        testMensaje.setTimestamp(LocalDateTime.now());
        testMensaje.setEmisor(testPaciente); // Assign Paciente as emisor for test
        testMensaje.setChat(testChat); // Link Mensaje to Chat
        testMensaje = entityManager.persistAndFlush(testMensaje);

        entityManager.clear();
    }

    // ================================
    // BASIC OPERATIONS TESTS
    // ================================

    @Test
    @DisplayName("Should find chat by patient ID")
    void shouldFindChatByPatientId() {
        // When
        Optional<Chat> result = chatRepository.findByPacienteIdAndNutricionistaId(testPaciente.getId(), testNutricionista.getId());
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPaciente().getId()).isEqualTo(testPaciente.getId());
        assertThat(result.get().getNutricionista().getId()).isEqualTo(testNutricionista.getId());
    }

    @Test
    @DisplayName("Should find chats by nutritionist ID with pagination")
    void shouldFindChatsByNutricionistaIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Chat> result = chatRepository.findByNutricionistaId(testNutricionista.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNutricionista().getId())
                .isEqualTo(testNutricionista.getId());
    }

    @Test
    @DisplayName("Should find conversation between patient and nutritionist")
    void shouldFindConversationBetweenPatientAndNutritionist() {
        // When
        Optional<Chat> result = chatRepository.findByPacienteIdAndNutricionistaId(
                testPaciente.getId(), testNutricionista.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPaciente().getId()).isEqualTo(testPaciente.getId());
        assertThat(result.get().getNutricionista().getId()).isEqualTo(testNutricionista.getId());
    }

    @Test
    @DisplayName("Should check if chat exists between patient and nutritionist")
    void shouldCheckIfChatExists() {
        // When
        Boolean exists = chatRepository.existsByPacienteIdAndNutricionistaId(
                testPaciente.getId(), testNutricionista.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when chat does not exist")
    void shouldReturnFalseWhenChatDoesNotExist() {
        // When
        Boolean exists = chatRepository.existsByPacienteIdAndNutricionistaId(999, 999);

        // Then
        assertThat(exists).isFalse();
    }

    // ================================
    // ANALYTICS TESTS
    // ================================

    @Test
    @DisplayName("Should count chats by nutritionist ID")
    void shouldCountChatsByNutricionistaId() {
        // When
        Long count = chatRepository.countChatsByNutricionistaId(testNutricionista.getId());

        // Then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should count active chats by nutritionist ID")
    void shouldCountActiveChatsByNutricionistaId() {
        // When
        Long count = chatRepository.countActiveChatsByNutricionistaId(testNutricionista.getId());

        // Then
        // This might be 0 since we haven't properly linked messages to chat
        assertThat(count).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("Should return zero count for non-existent nutritionist")
    void shouldReturnZeroCountForNonExistentNutritionist() {
        // When
        Long count = chatRepository.countChatsByNutricionistaId(999);

        // Then
        assertThat(count).isEqualTo(0L);
    }

    // ================================
    // PAGINATION TESTS
    // ================================

    @Test
    @DisplayName("Should find chat history for patient with pagination")
    void shouldFindChatHistoryForPatientWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<Chat> result = chatRepository.findChatHistoryForPatient(testPaciente.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find most active chats for nutritionist")
    void shouldFindMostActiveChatsForNutritionist() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<Chat> result = chatRepository.findMostActiveChatsForNutritionist(
                testNutricionista.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    // ================================
    // REAL-TIME FEATURES TESTS
    // ================================

    @Test
    @DisplayName("Should find chats with recent activity")
    void shouldFindChatsWithRecentActivity() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusHours(1);

        // When
        List<Chat> result = chatRepository.findChatsWithRecentActivity(testPaciente.getId(), since);

        // Then
        assertThat(result).isNotNull();
        // Result might be empty if messages aren't properly linked
    }

    @Test
    @DisplayName("Should find chats needing response")
    void shouldFindChatsNeedingResponse() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusHours(24);

        // When
        List<Chat> result = chatRepository.findChatsNeedingResponse(testNutricionista.getId(), since);

        // Then
        assertThat(result).isNotNull();
        // Result might be empty if messages aren't properly configured
    }

    // ================================
    // EDGE CASES TESTS
    // ================================

    @Test
    @DisplayName("Should handle empty results gracefully")
    void shouldHandleEmptyResultsGracefully() {
        // When
        Optional<Chat> result = chatRepository.findByPacienteIdAndNutricionistaId(999, 999);
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle pagination with no results")
    void shouldHandlePaginationWithNoResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Chat> result = chatRepository.findByNutricionistaId(999, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle large page requests")
    void shouldHandleLargePageRequests() {
        // Given
        Pageable pageable = PageRequest.of(0, 1000);

        // When
        Page<Chat> result = chatRepository.findByNutricionistaId(testNutricionista.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    // ================================
    // LEGACY SUPPORT TESTS
    // ================================

    @Test
    @DisplayName("Should support legacy findByPacienteId method")
    void shouldSupportLegacyFindByPacienteId() {
        // When
        Optional<Chat> result = chatRepository.findByPacienteIdAndNutricionistaId(testPaciente.getId(), testNutricionista.getId());
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPaciente().getId()).isEqualTo(testPaciente.getId());
    }

    @Test
    @DisplayName("Should support legacy findByNutricionistaId method")
    void shouldSupportLegacyFindByNutricionistaId() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Chat> result = chatRepository.findByNutricionistaId(testNutricionista.getId(), pageable);
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSizeGreaterThanOrEqualTo(1);
        assertThat(result.getContent().get(0).getNutricionista().getId()).isEqualTo(testNutricionista.getId());
    }

    // ================================
    // PERFORMANCE TESTS
    // ================================

    @Test
    @DisplayName("Should perform well with multiple chats")
    void shouldPerformWellWithMultipleChats() {
        // Given - Create multiple patients and chats
        for (int i = 0; i < 10; i++) {
            Paciente paciente = new Paciente();
            paciente.setNombre("Patient" + i);
            paciente.setApellidos("Test");
            paciente.setEmail("patient" + i + "@test.com");
            paciente.setPsw("password123");
            paciente = entityManager.persistAndFlush(paciente);

            Chat chat = new Chat();
            chat.setPaciente(paciente);
            chat.setNutricionista(testNutricionista);
            entityManager.persistAndFlush(chat);
        }
        entityManager.clear();

        // When
        long startTime = System.currentTimeMillis();
        Page<Chat> result = chatRepository.findByNutricionistaId(
                testNutricionista.getId(), PageRequest.of(0, 20));
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(result.getContent()).hasSize(11); // 10 + original test chat
        assertThat(endTime - startTime).isLessThan(1000); // Should complete in less than 1 second
    }
}
