package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.AntecedenteTratamientoDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;

@Mapper(componentModel = "spring")
public interface AntecedenteTratamientoMapper {
    AntecedenteTratamientoMapper INSTANCE = Mappers.getMapper(AntecedenteTratamientoMapper.class);
    
    @Mapping(source = "paciente.id", target = "idPaciente")
    AntecedenteTratamientoDTO toDto(AntecedenteTratamiento antecedenteTratamiento);
    
    List<AntecedenteTratamientoDTO> toDtoList(List<AntecedenteTratamiento> antecedenteTratamientoList);
    
    @Mapping(source = "idPaciente", target = "paciente.id")
    AntecedenteTratamiento toEntity(AntecedenteTratamientoDTO antecedenteTratamientoDTO);
    
    List<AntecedenteTratamiento> toEntityList(List<AntecedenteTratamientoDTO> antecedenteTratamientoDTOList);
}
