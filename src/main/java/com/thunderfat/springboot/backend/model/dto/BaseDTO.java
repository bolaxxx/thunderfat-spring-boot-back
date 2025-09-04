package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Clase base para DTOs que contiene campos comunes
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {
    
    private Integer id;
    private LocalDateTime createtime;
    private LocalDateTime updatetime;
    @Builder.Default
    private Boolean active = true;
    
    public boolean isNew() {
        return this.id == null;
    }
}
