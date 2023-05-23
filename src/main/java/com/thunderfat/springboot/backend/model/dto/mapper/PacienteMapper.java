package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.Rol;
import com.thunderfat.springboot.backend.model.entity.Usuario;

@Mapper
public interface PacienteMapper {
    PacienteMapper INSTANCE = Mappers.getMapper(PacienteMapper.class);

    @Mapping(source = "nutricionista.id", target = "nutricionistaId")
    @Mapping(source = "citas", target = "citasIds")
    @Mapping(source = "medicionesespecificas", target = "medicionesEspecificasIds")
    @Mapping(source = "medicionesgenerales", target = "medicionesGeneralesIds")
    @Mapping(source = "medicionessegmentales", target = "medicionesSegmentalesIds")
    @Mapping(source = "antecedentesclinicos", target = "antecedentesClinicosIds")
    @Mapping(source = "antecedentestratamientos", target = "antecedentesTratamientosIds")
    @Mapping(source = "planesdieta", target = "planesDietaIds")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "psw", target = "psw") // Cambiar "password" por "psw"
    @Mapping(source = "createtime", target = "createtime")
    @Mapping(source = "enabled", target = "enabled")
    PacienteDTO toDto(Paciente paciente);

    /**
     * @param pacienteDTO
     * @return
     */
    @Mapping(target = "nutricionista.id", source = "nutricionistaId")
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "medicionesespecificas", ignore = true)
    @Mapping(target = "medicionesgenerales", ignore = true)
    @Mapping(target = "medicionessegmentales", ignore = true)
    @Mapping(target = "antecedentesclinicos", ignore = true)
    @Mapping(target = "antecedentestratamientos", ignore = true)
    @Mapping(target = "planesdieta", ignore = true)
    Paciente toEntity(PacienteDTO pacienteDTO);


    /**
     * @param pacienteDTO
     * @return
     */
    @Mapping(target = "roles", source = "roles") // Corregir "rolesIds" a "roles"
    @Mapping(target = "email", source = "email")
    @Mapping(target = "psw", source = "password")
    @Mapping(target = "createtime", source = "createtime")
    @Mapping(target = "enabled", source = "enabled")
    Usuario toUsuarioEntity(PacienteDTO pacienteDTO);

    default List<Integer> mapCitasToIds(List<Cita> citas) {
        return citas.stream()
                .map(Cita::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapMedicionesEspecificasToIds(List<MedicionEspecifica> medicionesEspecificas) {
        return medicionesEspecificas.stream()
                .map(MedicionEspecifica::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapMedicionesGeneralesToIds(List<MedicionGeneral> medicionesGenerales) {
        return medicionesGenerales.stream()
                .map(MedicionGeneral::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapMedicionesSegmentalesToIds(List<MedicionSegmental> medicionesSegmentales) {
        return medicionesSegmentales.stream()
                .map(MedicionSegmental::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapAntecedentesClinicosToIds(List<AntecedentesClinicos> antecedentesClinicos) {
        return antecedentesClinicos.stream()
                .map(AntecedentesClinicos::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapAntecedentesTratamientosToIds(List<AntecedenteTratamiento> antecedentesTratamientos) {
        return antecedentesTratamientos.stream()
                .map(AntecedenteTratamiento::getId)
                .collect(Collectors.toList());
    }

    default List<Integer> mapPlanesDietaToIds(List<PlanDieta> planesDieta) {
        return planesDieta.stream()
                .map(PlanDieta::getId)
                .collect(Collectors.toList());
    }
    default List<Rol> mapRoles(List<Integer> rolesIds) {
        return rolesIds.stream()
                .map(id -> {
                    Rol rol = new Rol();
                    rol.setId(id);
                    return rol;
                })
                .collect(Collectors.toList());
    }
    default List<Rol> mapRolesToEntities(List<Integer> rolesIds) {
        return rolesIds.stream()
                .map(id -> {
                    Rol rol = new Rol();
                    rol.setId(id);
                    return rol;
                })
                .collect(Collectors.toList());
    }
}
