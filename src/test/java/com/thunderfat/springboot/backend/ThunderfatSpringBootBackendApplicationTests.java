package com.thunderfat.springboot.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.thunderfat.springboot.backend.config.GlobalTestConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@Import(GlobalTestConfiguration.class)
class ThunderfatSpringBootBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
