package com.thunderfat.springboot.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for OpenAPI documentation endpoints
 * Validates that OpenAPI documentation and Swagger UI are properly configured
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class OpenApiDocumentationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that OpenAPI JSON documentation is available
     */
    @Test
    public void apiDocsEndpointShouldReturnOpenApiJson() throws Exception {
        mockMvc.perform(get("/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").value("3.1.0"))
                .andExpect(jsonPath("$.info.title").value("ThunderFat Nutrition Management API"))
                .andExpect(jsonPath("$.paths").exists())
                .andExpect(jsonPath("$.components.schemas").exists());
    }

    /**
     * Test that Swagger UI is available
     */
    @Test
    public void swaggerUiEndpointShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isFound()); // 302 redirect to /swagger-ui/index.html
    }
    
    /**
     * Test that PlatoPredeterminado endpoints are documented in OpenAPI
     */
    @Test
    public void openApiShouldDocumentPlatoPredeterminadoEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/platopredeterminado/api/v1/dishes/{id}']").exists())
                .andExpect(jsonPath("$.paths['/platopredeterminado/api/v1/nutritionists/{nutricionistaId}/dishes']").exists())
                .andExpect(jsonPath("$.components.schemas.PlatoPredeterminadoDTO").exists());
    }
}
