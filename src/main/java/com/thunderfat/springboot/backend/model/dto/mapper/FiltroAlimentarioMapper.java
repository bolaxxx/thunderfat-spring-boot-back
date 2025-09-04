package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.FiltroAlimentarioDTO;
import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;

@Mapper(componentModel = "spring", uses = {AlimentoMapper.class})
public abstract class FiltroAlimentarioMapper {
    
    @Autowired
    protected AlimentoMapper alimentoMapper;

    @Mapping(target = "alimentos", ignore = true)
    public abstract FiltroAlimentarioDTO toDTO(FiltroAlimentario filtroAlimentario);

    @Mapping(target = "alimentos", ignore = true)
    @Mapping(target = "id_nutricionista", ignore = true)
    public abstract FiltroAlimentario toEntity(FiltroAlimentarioDTO filtroAlimentarioDTO);

    @Mapping(target = "alimentos", expression = "java(mapAlimentos(filtroAlimentario.getAlimentos()))")
    public abstract FiltroAlimentarioDTO toDTOWithAlimentos(FiltroAlimentario filtroAlimentario);

    protected List<AlimentoDTO> mapAlimentos(List<Alimento> alimentos) {
        if (alimentos == null) {
            return null;
        }
        return alimentos.stream()
                .map(alimentoMapper::toDto)
                .collect(Collectors.toList());
    }
}
