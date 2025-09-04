package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;
import java.util.ArrayList;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.entity.Alimento;

/**
 * MapStruct mapper for Alimento entity and DTO conversions
 * Follows Spring Boot 2025 best practices for object mapping
 * Updated to support all nutritional fields with proper validation
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AlimentoMapper {
    
    /**
     * Converts Alimento entity to AlimentoDTO
     * Direct field mapping since DTO now matches entity structure
     */
    AlimentoDTO toDto(Alimento entity);
    
    /**
     * Converts list of entities to DTOs
     */
    List<AlimentoDTO> toDtoList(List<Alimento> entities);
    
    /**
     * Converts AlimentoDTO to Alimento entity for creation
     */
    @Mapping(target = "id", ignore = true) // ID should be generated
    Alimento toEntity(AlimentoDTO dto);
    
    /**
     * Converts list of DTOs to entities
     */
    List<Alimento> toEntityList(List<AlimentoDTO> dtos);
    
    /**
     * Updates existing entity with DTO data (for partial updates)
     * Only non-null values from DTO will update the entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // Never update ID
    void updateEntityFromDto(AlimentoDTO dto, @MappingTarget Alimento entity);
    
    /**
     * Creates a copy of DTO for creation operations (removes ID)
     */
    @Mapping(target = "id", ignore = true)
    AlimentoDTO toCreateDto(AlimentoDTO dto);
}