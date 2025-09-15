package com.thunderfat.springboot.backend;

import com.thunderfat.springboot.backend.config.GlobalTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Simple test to check if ApplicationContext can load with test security setup
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import(GlobalTestConfiguration.class)
class SimpleApplicationTest {

    @Test
    void contextLoadsWithTestSecurity() {
        // This test just checks if the ApplicationContext can load
        // with the test security configuration
    }
}
