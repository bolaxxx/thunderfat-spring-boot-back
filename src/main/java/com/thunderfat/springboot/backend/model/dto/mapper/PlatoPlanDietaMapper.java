package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PlatoPlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

/**
 * MapStruct mapper for {@link PlatoPlanDieta} and {@link PlatoPlanDietaDTO} conversion.
 * Uses IngredienteMapper for nested conversions.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Mapper(componentModel = "spring", uses = {IngredienteMapper.class})
public interface PlatoPlanDietaMapper {
    PlatoPlanDietaMapper INSTANCE = Mappers.getMapper(PlatoPlanDietaMapper.class);

    /**
     * Converts a PlatoPlanDieta entity to its DTO representation
     * 
     * @param platoPlanDieta the entity to convert
     * @return the resulting DTO
     */
    @Mappings({
        @Mapping(target = "comidaId", expression = "java(getComidaId(platoPlanDieta))"),
        @Mapping(target = "planDietaId", expression = "java(getPlanDietaId(platoPlanDieta))")
    })
    PlatoPlanDietaDTO toDto(PlatoPlanDieta platoPlanDieta);
    
    /**
     * Converts a PlatoPlanDietaDTO to its entity representation
     * 
     * @param dto the DTO to convert
     * @return the resulting entity
     */
    // Ignore detailed nutritional properties that are not in the DTO
    @Mappings({
        @Mapping(target = "colesterol", ignore = true),
        @Mapping(target = "sodio", ignore = true),
        @Mapping(target = "potasio", ignore = true),
        @Mapping(target = "calcio", ignore = true),
        @Mapping(target = "hierro", ignore = true),
        @Mapping(target = "magnesio", ignore = true),
        @Mapping(target = "fosforo", ignore = true),
        @Mapping(target = "zinc", ignore = true),
        @Mapping(target = "vitaminaA", ignore = true),
        @Mapping(target = "vitaminaC", ignore = true),
        @Mapping(target = "vitaminaD", ignore = true),
        @Mapping(target = "vitaminaB6", ignore = true),
        @Mapping(target = "vitaminaB12", ignore = true),
        @Mapping(target = "vitaminaE", ignore = true),
        @Mapping(target = "vitaminaK", ignore = true),
        @Mapping(target = "tiamina", ignore = true),
        @Mapping(target = "riboflavina", ignore = true),
        @Mapping(target = "niacina", ignore = true),
        @Mapping(target = "acfolico", ignore = true),
        @Mapping(target = "pantotenico", ignore = true),
        @Mapping(target = "biotina", ignore = true),
        @Mapping(target = "vitaminaB12_2", ignore = true),
        @Mapping(target = "vitaminaC_2", ignore = true),
        @Mapping(target = "sodio_2", ignore = true),
        @Mapping(target = "potasio_2", ignore = true),
        @Mapping(target = "calcio_2", ignore = true),
        @Mapping(target = "hierro_2", ignore = true),
        @Mapping(target = "magnesio_2", ignore = true),
        @Mapping(target = "fosforo_2", ignore = true),
        @Mapping(target = "zinc_2", ignore = true),
        @Mapping(target = "vitaminaA_2", ignore = true),
        @Mapping(target = "sal", ignore = true),
        @Mapping(target = "azucar", ignore = true)
    })
    PlatoPlanDieta toEntity(PlatoPlanDietaDTO dto);
    
    /**
     * Updates an existing PlatoPlanDieta entity with data from a DTO
     * 
     * @param dto the source of the data
     * @param entity the entity to update
     * @return the updated entity
     */
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "colesterol", ignore = true),
        @Mapping(target = "sodio", ignore = true),
        @Mapping(target = "potasio", ignore = true),
        @Mapping(target = "calcio", ignore = true),
        @Mapping(target = "hierro", ignore = true),
        @Mapping(target = "magnesio", ignore = true),
        @Mapping(target = "fosforo", ignore = true),
        @Mapping(target = "zinc", ignore = true),
        @Mapping(target = "vitaminaA", ignore = true),
        @Mapping(target = "vitaminaC", ignore = true),
        @Mapping(target = "vitaminaD", ignore = true),
        @Mapping(target = "vitaminaB6", ignore = true),
        @Mapping(target = "vitaminaB12", ignore = true),
        @Mapping(target = "vitaminaE", ignore = true),
        @Mapping(target = "vitaminaK", ignore = true),
        @Mapping(target = "tiamina", ignore = true),
        @Mapping(target = "riboflavina", ignore = true),
        @Mapping(target = "niacina", ignore = true),
        @Mapping(target = "acfolico", ignore = true),
        @Mapping(target = "pantotenico", ignore = true),
        @Mapping(target = "biotina", ignore = true),
        @Mapping(target = "vitaminaB12_2", ignore = true),
        @Mapping(target = "vitaminaC_2", ignore = true),
        @Mapping(target = "sodio_2", ignore = true),
        @Mapping(target = "potasio_2", ignore = true),
        @Mapping(target = "calcio_2", ignore = true),
        @Mapping(target = "hierro_2", ignore = true),
        @Mapping(target = "magnesio_2", ignore = true),
        @Mapping(target = "fosforo_2", ignore = true),
        @Mapping(target = "zinc_2", ignore = true),
        @Mapping(target = "vitaminaA_2", ignore = true),
        @Mapping(target = "sal", ignore = true),
        @Mapping(target = "azucar", ignore = true)
    })
    PlatoPlanDieta updateEntityFromDto(PlatoPlanDietaDTO dto, @MappingTarget PlatoPlanDieta entity);
    
    /**
     * Helper method to extract comida ID from PlatoPlanDieta
     */
    default Integer getComidaId(PlatoPlanDieta platoPlanDieta) {
        // This would need to be implemented based on how PlatoPlanDieta relates to Comida
        // For now, returning null since we don't have direct relationship in the entity
        return null;
    }
    
    /**
     * Helper method to extract plan dieta ID from PlatoPlanDieta
     */
    default Integer getPlanDietaId(PlatoPlanDieta platoPlanDieta) {
        // This would need to be implemented based on how PlatoPlanDieta relates to PlanDieta
        // For now, returning null since we don't have direct relationship in the entity
        return null;
    }
    
    // For backward compatibility with existing code
    default PlatoPlanDietaDTO toDTO(PlatoPlanDieta platoPlanDieta) {
        return toDto(platoPlanDieta);
    }
}
