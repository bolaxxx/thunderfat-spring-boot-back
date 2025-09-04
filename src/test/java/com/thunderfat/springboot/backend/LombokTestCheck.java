package com.thunderfat.springboot.backend;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Simple test to verify Lombok annotation processing is working
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LombokTestCheck {
    private String name;
    private int value;
    
    // Test if builder() method is generated
    public static void testLombok() {
        LombokTestCheck test = LombokTestCheck.builder()
            .name("Test")
            .value(42)
            .build();
        
        System.out.println("Lombok test: " + test.getName());
    }
}
