package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.DiaDietaDTO;
import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

@Mapper
public interface PlanDietaMapper {
    PlanDietaMapper INSTANCE = Mappers.getMapper(PlanDietaMapper.class);

    PlanDietaDTO toDTO(PlanDieta planDieta);

    PlanDieta toEntity(PlanDietaDTO planDietaDTO);

    @Mapping(target = "diaDietas", expression = "java(mapDiaDietas(planDieta.getDiaDietas()))")
    PlanDietaDTO toDTOWithDiaDietas(PlanDieta planDieta);

    default List<DiaDietaDTO> mapDiaDietas(List<DiaDieta> diaDietas) {
        return diaDietas.stream()
                .map(DiaDietaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // Otros métodos de mapeo
}
