package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.IngredienteDTO;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;

@Mapper(componentModel = "spring", uses = {AlimentoMapper.class})
public interface IngredienteMapper {
    IngredienteMapper INSTANCE = Mappers.getMapper(IngredienteMapper.class);

    IngredienteDTO toDTO(Ingrediente ingrediente);
    Ingrediente toEntity(IngredienteDTO ingredienteDTO);
}
