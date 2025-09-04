package com.thunderfat.springboot.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;

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
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
class PlatoPredeterminadoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private IPlatoPredetereminadoService service;
    
    @Test
    @DisplayName("GET /api/v1/dishes/{id} - Should return dish by ID")
    @Transactional
    void shouldReturnDishById() throws Exception {
        // This test assumes there's a dish with ID 1 in the test database
        mockMvc.perform(get("/platopredeterminado/api/v1/dishes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }
    
    @Test
    @DisplayName("GET /api/v1/dishes/{id} - Should return 404 when dish not found")
    void shouldReturn404WhenDishNotFound() throws Exception {
        mockMvc.perform(get("/platopredeterminado/api/v1/dishes/9999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Dish not found with id: 9999"));
    }
    
    @Test
    @DisplayName("POST /api/v1/nutritionists/{nutricionistaId}/dishes - Should create new dish")
    @Transactional
    void shouldCreateNewDish() throws Exception {
        // Assuming there's a nutritionist with ID 1 in the test database
        PlatoPredeterminadoDTO newDish = new PlatoPredeterminadoDTO();
        newDish.setNombre("Test Integration Dish");
        newDish.setReceta("Test recipe for integration");
        newDish.setNutricionistaId(1);
        newDish.setKcaltotales(500.0);
        
        MvcResult result = mockMvc.perform(post("/platopredeterminado/api/v1/nutritionists/1/dishes")
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
    @DisplayName("GET /api/v1/nutritionists/{nutricionistaId}/dishes - Should return dishes for nutritionist")
    @Transactional
    void shouldReturnDishesForNutritionist() throws Exception {
        // Assuming there's a nutritionist with ID 1 in the test database with dishes
        mockMvc.perform(get("/platopredeterminado/api/v1/nutritionists/1/dishes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
