package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.ComidaDTO;
import com.thunderfat.springboot.backend.model.dto.PlatoPlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

/**
 * MapStruct mapper for {@link Comida} and {@link ComidaDTO} conversion.
 * Uses PlatoPlanDietaMapper for nested conversions of platos collection.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Mapper(componentModel = "spring", uses = {})
public interface ComidaMapper {
    ComidaMapper INSTANCE = Mappers.getMapper(ComidaMapper.class);

    /**
     * Converts a Comida entity to its DTO representation
     * 
     * @param comida the entity to convert
     * @return the resulting DTO
     */
    @Mapping(target = "platos", source = "platos", qualifiedByName = "mapPlatosToDto")
    ComidaDTO toDto(Comida comida);
    
    /**
     * Converts a ComidaDTO to its entity representation
     * 
     * @param comidaDTO the DTO to convert
     * @return the resulting entity
     */
    @InheritInverseConfiguration
    @Mapping(target = "platos", source = "platos", qualifiedByName = "mapPlatosToEntity")
    Comida toEntity(ComidaDTO comidaDTO);
    
    /**
     * Converts a list of Comida entities to DTOs
     * 
     * @param comidas the list of entities to convert
     * @return list of converted DTOs
     */
    List<ComidaDTO> toDtoList(List<Comida> comidas);
    
    /**
     * Converts a list of ComidaDTOs to entities
     * 
     * @param comidaDTOs the list of DTOs to convert
     * @return list of converted entities
     */
    List<Comida> toEntityList(List<ComidaDTO> comidaDTOs);
    
    /**
     * Updates an existing Comida entity with values from a DTO
     * 
     * @param comidaDTO the source DTO
     * @param comida the target entity to update
     */
    @Mapping(target = "platos", source = "platos", qualifiedByName = "mapPlatosToEntity")
    void updateEntityFromDto(ComidaDTO comidaDTO, @MappingTarget Comida comida);
    
    /**
     * Maps PlatoPlanDieta entities to DTOs manually to avoid ambiguous mapping methods
     * 
     * @param platos list of PlatoPlanDieta entities
     * @return list of PlatoPlanDietaDTO objects
     */
    @org.mapstruct.Named("mapPlatosToDto")
    default List<PlatoPlanDietaDTO> mapPlatosToDto(List<PlatoPlanDieta> platos) {
        if (platos == null) {
            return null;
        }
        
        PlatoPlanDietaMapper platoPlanDietaMapper = PlatoPlanDietaMapper.INSTANCE;
        return platos.stream()
                .map(platoPlanDietaMapper::toDto)
                .toList();
    }
    
    /**
     * Maps PlatoPlanDietaDTO objects to entities manually to avoid ambiguous mapping methods
     * 
     * @param platoDTOs list of PlatoPlanDietaDTO objects
     * @return list of PlatoPlanDieta entities
     */
    @org.mapstruct.Named("mapPlatosToEntity")
    default List<PlatoPlanDieta> mapPlatosToEntity(List<PlatoPlanDietaDTO> platoDTOs) {
        if (platoDTOs == null) {
            return null;
        }
        
        PlatoPlanDietaMapper platoPlanDietaMapper = PlatoPlanDietaMapper.INSTANCE;
        return platoDTOs.stream()
                .map(platoPlanDietaMapper::toEntity)
                .toList();
    }
}
