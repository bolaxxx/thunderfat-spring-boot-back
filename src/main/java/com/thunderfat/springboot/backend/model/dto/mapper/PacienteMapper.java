package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

@Mapper
public interface PacienteMapper {
    PacienteMapper INSTANCE = Mappers.getMapper(PacienteMapper.class);

    @Mapping(source = "nutricionista.id", target = "nutricionistaId")
    @Mapping(source = "citas", target = "citasIds", qualifiedByName = "mapCitasToIds")
    @Mapping(source = "medicionesespecificas", target = "medicionesEspecificasIds", qualifiedByName = "mapMedicionesEspecificasToIds")
    @Mapping(source = "medicionesgenerales", target = "medicionesGeneralesIds", qualifiedByName = "mapMedicionesGeneralesToIds")
    @Mapping(source = "medicionessegmentales", target = "medicionesSegmentalesIds", qualifiedByName = "mapMedicionesSegmentalesToIds")
    @Mapping(source = "antecedentesclinicos", target = "antecedentesClinicosIds", qualifiedByName = "mapAntecedentesClinicosToIds")
    @Mapping(source = "antecedentestratamientos", target = "antecedentesTratamientosIds", qualifiedByName = "mapAntecedentesTratamientosToIds")
    @Mapping(source = "planesdieta", target = "planesDietaIds", qualifiedByName = "mapPlanesDietaToIds")
    @Mapping(target = "psw", ignore = true) // Nunca mapear la contraseña al DTO de salida
    PacienteDTO toDto(Paciente paciente);

    @Mapping(source = "nutricionistaId", target = "nutricionista", qualifiedByName = "mapNutricionistaId")
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "medicionesespecificas", ignore = true)
    @Mapping(target = "medicionesgenerales", ignore = true)
    @Mapping(target = "medicionessegmentales", ignore = true)
    @Mapping(target = "antecedentesclinicos", ignore = true)
    @Mapping(target = "antecedentestratamientos", ignore = true)
    @Mapping(target = "planesdieta", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Paciente toEntity(PacienteDTO pacienteDTO);

    @Named("mapCitasToIds")
    default List<Integer> mapCitasToIds(List<Cita> citas) {
        return Optional.ofNullable(citas)
                .orElse(Collections.emptyList())
                .stream()
                .map(Cita::getId)
                .collect(Collectors.toList());
    }

    @Named("mapMedicionesEspecificasToIds")
    default List<Integer> mapMedicionesEspecificasToIds(List<MedicionEspecifica> medicionesEspecificas) {
        return Optional.ofNullable(medicionesEspecificas)
                .orElse(Collections.emptyList())
                .stream()
                .map(MedicionEspecifica::getId)
                .collect(Collectors.toList());
    }

    @Named("mapMedicionesGeneralesToIds")
    default List<Integer> mapMedicionesGeneralesToIds(List<MedicionGeneral> medicionesGenerales) {
        return Optional.ofNullable(medicionesGenerales)
                .orElse(Collections.emptyList())
                .stream()
                .map(MedicionGeneral::getId)
                .collect(Collectors.toList());
    }

    @Named("mapMedicionesSegmentalesToIds")
    default List<Integer> mapMedicionesSegmentalesToIds(List<MedicionSegmental> medicionesSegmentales) {
        return Optional.ofNullable(medicionesSegmentales)
                .orElse(Collections.emptyList())
                .stream()
                .map(MedicionSegmental::getId)
                .collect(Collectors.toList());
    }

    @Named("mapAntecedentesClinicosToIds")
    default List<Integer> mapAntecedentesClinicosToIds(List<AntecedentesClinicos> antecedentesClinicos) {
        return Optional.ofNullable(antecedentesClinicos)
                .orElse(Collections.emptyList())
                .stream()
                .map(AntecedentesClinicos::getId)
                .collect(Collectors.toList());
    }

    @Named("mapAntecedentesTratamientosToIds")
    default List<Integer> mapAntecedentesTratamientosToIds(List<AntecedenteTratamiento> antecedentesTratamientos) {
        return Optional.ofNullable(antecedentesTratamientos)
                .orElse(Collections.emptyList())
                .stream()
                .map(AntecedenteTratamiento::getId)
                .collect(Collectors.toList());
    }

    @Named("mapPlanesDietaToIds")
    default List<Integer> mapPlanesDietaToIds(List<PlanDieta> planesDieta) {
        return Optional.ofNullable(planesDieta)
                .orElse(Collections.emptyList())
                .stream()
                .map(PlanDieta::getId)
                .collect(Collectors.toList());
    }

    @Named("mapNutricionistaId")
    default Nutricionista mapNutricionistaId(Integer nutricionistaId) {
        if (nutricionistaId == null) {
            return null;
        }
        Nutricionista nutricionista = new Nutricionista();
        nutricionista.setId(nutricionistaId);
        return nutricionista;
    }
}
