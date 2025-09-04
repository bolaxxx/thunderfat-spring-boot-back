package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

@Mapper(componentModel = "spring")
public interface PlatoPredeterminadoMapper {
    PlatoPredeterminadoMapper INSTANCE = Mappers.getMapper(PlatoPredeterminadoMapper.class);
    
    /**
     * Map from entity to DTO, handling the nutricionista relationship
     */
    @Mapping(target = "ingredientes", ignore = true) // Ignore complex ingredient mapping
    @Mapping(target = "nutricionistaId", source = "nutricionista.id")
    PlatoPredeterminadoDTO toDto(PlatoPredeterminado platoPredeterminado);
    
    /**
     * Map from entity list to DTO list
     */
    List<PlatoPredeterminadoDTO> toDtoList(List<PlatoPredeterminado> platosPredeterminados);
    
    @Mapping(target = "ingredientes", ignore = true) // Ignore complex ingredient mapping
    @Mapping(target = "colesterol", ignore = true)
    @Mapping(target = "sodio", ignore = true)
    @Mapping(target = "potasio", ignore = true)
    @Mapping(target = "calcio", ignore = true)
    @Mapping(target = "hierro", ignore = true)
    @Mapping(target = "magnesio", ignore = true)
    @Mapping(target = "fosforo", ignore = true)
    @Mapping(target = "zinc", ignore = true)
    @Mapping(target = "vitaminaA", ignore = true)
    @Mapping(target = "vitaminaC", ignore = true)
    @Mapping(target = "vitaminaD", ignore = true)
    @Mapping(target = "vitaminaB6", ignore = true)
    @Mapping(target = "vitaminaB12", ignore = true)
    @Mapping(target = "vitaminaE", ignore = true)
    @Mapping(target = "vitaminaK", ignore = true)
    @Mapping(target = "tiamina", ignore = true)
    @Mapping(target = "riboflavina", ignore = true)
    @Mapping(target = "niacina", ignore = true)
    @Mapping(target = "acfolico", ignore = true)
    @Mapping(target = "pantotenico", ignore = true)
    @Mapping(target = "biotina", ignore = true)
    @Mapping(target = "vitaminaB12_2", ignore = true)
    @Mapping(target = "vitaminaC_2", ignore = true)
    @Mapping(target = "sodio_2", ignore = true)
    @Mapping(target = "potasio_2", ignore = true)
    @Mapping(target = "calcio_2", ignore = true)
    @Mapping(target = "hierro_2", ignore = true)
    @Mapping(target = "magnesio_2", ignore = true)
    @Mapping(target = "fosforo_2", ignore = true)
    @Mapping(target = "zinc_2", ignore = true)
    @Mapping(target = "vitaminaA_2", ignore = true)
    @Mapping(target = "nutricionista", ignore = true) // This will be set manually in the service
    PlatoPredeterminado toEntity(PlatoPredeterminadoDTO platoPredeterminadoDTO);
    
    List<PlatoPredeterminado> toEntityList(List<PlatoPredeterminadoDTO> platosPredeterminadosDTO);
}
