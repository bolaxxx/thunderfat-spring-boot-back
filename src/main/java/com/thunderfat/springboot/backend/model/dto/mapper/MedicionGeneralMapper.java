package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.MedicionGeneralDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;

@Mapper(componentModel = "spring")
public interface MedicionGeneralMapper {
    MedicionGeneralMapper INSTANCE = Mappers.getMapper(MedicionGeneralMapper.class);
    
    @Mapping(target = "paciente", ignore = true)
    MedicionGeneralDTO toDto(MedicionGeneral medicionGeneral);
    
    List<MedicionGeneralDTO> toDtoList(List<MedicionGeneral> medicionesGenerales);
    
    @Mapping(target = "id_paciente", ignore = true)
    MedicionGeneral toEntity(MedicionGeneralDTO medicionGeneralDTO);
    
    List<MedicionGeneral> toEntityList(List<MedicionGeneralDTO> medicionesGeneralesDTO);
}
