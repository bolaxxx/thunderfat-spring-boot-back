package com.thunderfat.springboot.backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.thunderfat.springboot.backend.config.GlobalTestConfiguration;
import com.thunderfat.springboot.backend.model.service.IAlimentoService;

/**
 * Simple context loading test for AlimentoRestController
 */
@WebMvcTest(AlimentoRestController.class)
@ActiveProfiles("test")
@Import(GlobalTestConfiguration.class)
class AlimentoRestControllerContextTest {
    
    @MockBean
    private IAlimentoService alimentoService;
    
    @Test
    void contextLoads() {
        // This test will pass if the ApplicationContext loads successfully
    }
}
