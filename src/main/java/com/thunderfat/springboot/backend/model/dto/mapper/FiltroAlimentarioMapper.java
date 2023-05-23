package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.FiltroAlimentarioDTO;
import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;

@Mapper
public interface FiltroAlimentarioMapper {
    FiltroAlimentarioMapper INSTANCE = Mappers.getMapper(FiltroAlimentarioMapper.class);

    FiltroAlimentarioDTO toDTO(FiltroAlimentario filtroAlimentario);

    FiltroAlimentario toEntity(FiltroAlimentarioDTO filtroAlimentarioDTO);

    @Mapping(target = "alimentos", expression = "java(mapAlimentos(filtroAlimentario.getAlimentos()))")
    FiltroAlimentarioDTO toDTOWithAlimentos(FiltroAlimentario filtroAlimentario);

    default List<AlimentoDTO> mapAlimentos(List<Alimento> alimentos) {
        return alimentos.stream()
                .map(AlimentoMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
}
