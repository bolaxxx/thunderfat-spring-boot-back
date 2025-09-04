package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Mapper
public interface NutricionistaMapper {
    NutricionistaMapper INSTANCE = Mappers.getMapper(NutricionistaMapper.class);

    @Mapping(source = "pacientes", target = "pacientesIds", qualifiedByName = "mapPacientesToIds")
    @Mapping(source = "citas", target = "citasIds", qualifiedByName = "mapCitasToIds")
    @Mapping(target = "psw", ignore = true) // Nunca mapear la contraseña al DTO de salida
   @Mapping(source = "numeroColegiadoProfesional", target = "numeroColegiadoProfesional")
    NutricionistaDTO toDTO(Nutricionista nutricionista);

    @Mapping(target = "pacientes", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
   @Mapping(source = "numeroColegiadoProfesional", target = "numeroColegiadoProfesional")
    Nutricionista toEntity(NutricionistaDTO nutricionistaDTO);

    @Named("mapPacientesToIds")
    default List<Integer> mapPacientesToIds(List<Paciente> pacientes) {
        return Optional.ofNullable(pacientes)
                .orElse(Collections.emptyList())
                .stream()
                .map(Paciente::getId)
                .collect(Collectors.toList());
    }

    @Named("mapCitasToIds")
    default List<Integer> mapCitasToIds(List<Cita> citas) {
        return Optional.ofNullable(citas)
                .orElse(Collections.emptyList())
                .stream()
                .map(Cita::getId)
                .collect(Collectors.toList());
    }
}