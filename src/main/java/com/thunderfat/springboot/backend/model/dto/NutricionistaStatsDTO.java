package com.thunderfat.springboot.backend.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for nutritionist statistics related to diet plans.
 * Contains analytics data for dashboard displays.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Data
@Builder
public class NutricionistaStatsDTO {
    
    /**
     * Total number of diet plans created by the nutritionist.
     */
    private Long totalPlans;
    
    /**
     * Number of currently active diet plans.
     */
    private Long activePlans;
    
    /**
     * Number of expired diet plans.
     */
    private Long expiredPlans;
    
    /**
     * Average duration of diet plans in days.
     */
    private Double averagePlanDuration;
    
    /**
     * Total number of patients served.
     */
    private Long totalPatients;
    
    /**
     * Number of patients with currently active plans.
     */
    private Long activePatientsCount;
    
    /**
     * Number of plans expiring within the next 7 days.
     */
    private Long plansSoonToExpire;
}
