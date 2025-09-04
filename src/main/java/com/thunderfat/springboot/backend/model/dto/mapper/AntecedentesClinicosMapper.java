package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.AntecedentesClinicosDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;

@Mapper(componentModel = "spring")
public interface AntecedentesClinicosMapper {
    AntecedentesClinicosMapper INSTANCE = Mappers.getMapper(AntecedentesClinicosMapper.class);

    // Maps paciente.id from entity to idPaciente in DTO
    @Mapping(source = "paciente.id", target = "idPaciente")
    AntecedentesClinicosDTO toDto(AntecedentesClinicos antecedentesClinicos);

    List<AntecedentesClinicosDTO> toDtoList(List<AntecedentesClinicos> antecedentesClinicosList);

    // Ignore paciente in reverse mapping to avoid mapping nested objects directly
    @Mapping(target = "paciente", ignore = true)
    AntecedentesClinicos toEntity(AntecedentesClinicosDTO antecedentesClinicosDTO);

    List<AntecedentesClinicos> toEntityList(List<AntecedentesClinicosDTO> antecedentesClinicosDTOList);
}
