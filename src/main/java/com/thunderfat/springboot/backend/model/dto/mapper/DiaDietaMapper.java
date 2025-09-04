package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.DiaDietaDTO;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;

@Mapper(componentModel = "spring", uses = {ComidaMapper.class})
public interface DiaDietaMapper {
    DiaDietaMapper INSTANCE = Mappers.getMapper(DiaDietaMapper.class);

    DiaDietaDTO toDTO(DiaDieta diaDieta);
    DiaDieta toEntity(DiaDietaDTO diaDietaDTO);
}
