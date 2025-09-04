package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.MedicionSegmentalDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;

@Mapper(componentModel = "spring")
public interface MedicionSegmentalMapper {
    MedicionSegmentalMapper INSTANCE = Mappers.getMapper(MedicionSegmentalMapper.class);
    
    MedicionSegmentalDTO toDto(MedicionSegmental medicionSegmental);
    
    List<MedicionSegmentalDTO> toDtoList(List<MedicionSegmental> medicionesSegmentales);
    
    @Mapping(target = "id_paciente", ignore = true)
    MedicionSegmental toEntity(MedicionSegmentalDTO medicionSegmentalDTO);
    
    List<MedicionSegmental> toEntityList(List<MedicionSegmentalDTO> medicionesSegmentalesDTO);
}
