package com.thunderfat.springboot.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thunderfat.springboot.backend.config.GlobalTestConfiguration;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;

import jakarta.persistence.EntityManager;

/**
 * Integration tests for the PlatoPredeterminado endpoints.
 * Demonstrates Spring Boot 2025 integration testing best practices:
 * - Using @SpringBootTest for full application context
 * - @Transactional for test isolation
 * - MockMvc for endpoint testing
 * - Custom ActiveProfile for testing environment
 * - Full end-to-end testing of REST endpoints
 * 
 * @author ThunderFat Development Team
 */
@SpringBootTest(properties = {"spring.cache.type=none"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(GlobalTestConfiguration.class)
@Tag("integration")
class PlatoPredeterminadoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IPlatoPredetereminadoService service;
    
    @Autowired
    private EntityManager entityManager;
    
    /**
     * Helper method to create test data programmatically.
     * This is more reliable than SQL scripts with complex entity inheritance.
     */
    private int createTestData() {
        // Create a test nutritionist
        Nutricionista nutricionista = new Nutricionista();
        // Don't set ID - let Hibernate auto-generate it
        // Set basic fields required by Usuario parent class
        nutricionista.setEmail("test-nutri@example.com");
        nutricionista.setPsw("testpassword");
        nutricionista.setEnabled(true);
        
        // Set specific Nutricionista fields
        nutricionista.setNombre("Test Nutricionista");
        nutricionista.setApellidos("Test Apellidos");
        nutricionista.setNumeroColegiadoProfesional("COL123");
        
        entityManager.persist(nutricionista);
        entityManager.flush(); // Ensure ID is generated
        
        // Create a test predetermined dish
        PlatoPredeterminado plato = new PlatoPredeterminado();
        // Don't set ID - let Hibernate auto-generate it
        plato.setNombre("Test Dish");
        plato.setReceta("Test recipe");
        plato.setKcaltotales(300.0);
        plato.setNutricionista(nutricionista);
        
        entityManager.persist(plato);
        entityManager.flush(); // Ensure ID is generated
        
        return plato.getId(); // Return the generated ID for testing
    }
    
    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("GET /api/v1/dishes/{id} - Should return dish by ID")
    @Transactional
    void shouldReturnDishById() throws Exception {
        // Create test data programmatically and get the generated ID
        int dishId = createTestData();
        
        // Debug: Check if the test data exists
        System.out.println("=== DEBUG: Checking if dish with ID " + dishId + " exists ===");
        Optional<PlatoPredeterminadoDTO> dish = service.findById(dishId);
        System.out.println("Dish exists: " + dish.isPresent());
        if (dish.isPresent()) {
            System.out.println("Dish details: " + dish.get());
        }
        
        // First verify the dish exists in the service
        assertThat(dish).isPresent();
        
        // Then test the REST endpoint
        mockMvc.perform(get("/platopredeterminado/api/v1/dishes/" + dishId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(dishId));
    }
    
    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("GET /api/v1/dishes/{id} - Should return 404 when dish not found")
    void shouldReturn404WhenDishNotFound() throws Exception {
        mockMvc.perform(get("/platopredeterminado/api/v1/dishes/9999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Dish not found with id: 9999"));
    }
    
    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("POST /api/v1/nutritionists/{nutricionistaId}/dishes - Should create new dish")
    @Transactional
    void shouldCreateNewDish() throws Exception {
        // Create test nutritionist first and get the generated ID
        int dishId = createTestData(); // This creates nutritionist and one dish
        // Get the nutritionist ID from the created dish
        Optional<PlatoPredeterminadoDTO> existingDish = service.findById(dishId);
        assertThat(existingDish).isPresent();
        int nutricionistaId = existingDish.get().getNutricionistaId();
        
        // Create a new dish for the same nutritionist
        PlatoPredeterminadoDTO newDish = new PlatoPredeterminadoDTO();
        newDish.setNombre("Test Integration Dish");
        newDish.setReceta("Test recipe for integration");
        newDish.setNutricionistaId(nutricionistaId);
        newDish.setKcaltotales(500.0);
        
        MvcResult result = mockMvc.perform(post("/platopredeterminado/api/v1/nutritionists/" + nutricionistaId + "/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDish))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test Integration Dish"))
                .andReturn();
        
        // Extract the ID of the created dish to verify it exists in the database
        String content = result.getResponse().getContentAsString();
        Integer createdDishId = objectMapper.readTree(content)
                .path("data")
                .path("id")
                .asInt();
        
        // Verify the dish exists in the database
        assertThat(service.findById(createdDishId)).isPresent();
    }
    
    @Test
    @WithMockUser(roles = "NUTRICIONISTA")
    @DisplayName("GET /api/v1/nutritionists/{nutricionistaId}/dishes - Should return dishes for nutritionist")
    @Transactional
    void shouldReturnDishesForNutritionist() throws Exception {
        // Create test nutritionist and dish first
        int dishId = createTestData(); // This creates nutritionist and one dish
        // Get the nutritionist ID from the created dish
        Optional<PlatoPredeterminadoDTO> existingDish = service.findById(dishId);
        assertThat(existingDish).isPresent();
        int nutricionistaId = existingDish.get().getNutricionistaId();
        
        // Test the endpoint with the actual nutritionist ID
        mockMvc.perform(get("/platopredeterminado/api/v1/nutritionists/" + nutricionistaId + "/dishes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
