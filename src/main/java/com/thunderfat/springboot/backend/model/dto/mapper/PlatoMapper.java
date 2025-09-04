package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PlatoDTO;
import com.thunderfat.springboot.backend.model.entity.Plato;

@Mapper(componentModel = "spring", uses = {IngredienteMapper.class})
public interface PlatoMapper {
    PlatoMapper INSTANCE = Mappers.getMapper(PlatoMapper.class);
    
    PlatoDTO toDto(Plato plato);
    
    List<PlatoDTO> toDtoList(List<Plato> platos);
    
    @Mapping(target = "ingredientes", ignore = true)
    @Mapping(target = "acfolico", ignore = true)
    @Mapping(target = "biotina", ignore = true)
    @Mapping(target = "calcio", ignore = true)
    @Mapping(target = "calcio_2", ignore = true)
    @Mapping(target = "colesterol", ignore = true)
    @Mapping(target = "fosforo", ignore = true)
    @Mapping(target = "fosforo_2", ignore = true)
    @Mapping(target = "hierro", ignore = true)
    @Mapping(target = "hierro_2", ignore = true)
    @Mapping(target = "magnesio", ignore = true)
    @Mapping(target = "magnesio_2", ignore = true)
    @Mapping(target = "niacina", ignore = true)
    @Mapping(target = "pantotenico", ignore = true)
    @Mapping(target = "potasio", ignore = true)
    @Mapping(target = "potasio_2", ignore = true)
    @Mapping(target = "riboflavina", ignore = true)
    @Mapping(target = "sodio", ignore = true)
    @Mapping(target = "sodio_2", ignore = true)
    @Mapping(target = "tiamina", ignore = true)
    @Mapping(target = "vitaminaA", ignore = true)
    @Mapping(target = "vitaminaA_2", ignore = true)
    @Mapping(target = "vitaminaB12", ignore = true)
    @Mapping(target = "vitaminaB12_2", ignore = true)
    @Mapping(target = "vitaminaB6", ignore = true)
    @Mapping(target = "vitaminaC", ignore = true)
    @Mapping(target = "vitaminaC_2", ignore = true)
    @Mapping(target = "vitaminaD", ignore = true)
    @Mapping(target = "vitaminaE", ignore = true)
    @Mapping(target = "vitaminaK", ignore = true)
    @Mapping(target = "zinc", ignore = true)
    @Mapping(target = "zinc_2", ignore = true)
    Plato toEntity(PlatoDTO platoDTO);
    
    List<Plato> toEntityList(List<PlatoDTO> platosDTO);
}
