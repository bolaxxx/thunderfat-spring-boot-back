package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.MedicionEspecificaDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;

@Mapper(componentModel = "spring")
public interface MedicionEspecificaMapper {
    MedicionEspecificaMapper INSTANCE = Mappers.getMapper(MedicionEspecificaMapper.class);
    
    MedicionEspecificaDTO toDto(MedicionEspecifica medicionEspecifica);
    
    List<MedicionEspecificaDTO> toDtoList(List<MedicionEspecifica> medicionesEspecificas);
    
    @Mapping(target = "id_paciente", ignore = true)
    MedicionEspecifica toEntity(MedicionEspecificaDTO medicionEspecificaDTO);
    
    List<MedicionEspecifica> toEntityList(List<MedicionEspecificaDTO> medicionesEspecificasDTO);
}
