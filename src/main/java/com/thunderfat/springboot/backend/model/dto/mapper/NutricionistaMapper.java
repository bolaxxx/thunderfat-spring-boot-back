package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;

@Mapper
public interface NutricionistaMapper {
    NutricionistaMapper INSTANCE = Mappers.getMapper(NutricionistaMapper.class);

    NutricionistaDTO toDTO(Nutricionista nutricionista);

    Nutricionista toEntity(NutricionistaDTO nutricionistaDTO);
}