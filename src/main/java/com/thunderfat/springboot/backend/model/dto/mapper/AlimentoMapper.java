package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.entity.Alimento;

@Mapper
public interface AlimentoMapper {
    AlimentoMapper INSTANCE = Mappers.getMapper(AlimentoMapper.class);

    AlimentoDTO toDTO(Alimento alimento);

    Alimento toEntity(AlimentoDTO alimentoDTO);
}