package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.CitaDTO;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Mapper(componentModel = "spring")
public interface CitaMapper {
    CitaMapper INSTANCE = Mappers.getMapper(CitaMapper.class);

    @Mapping(source = "fechaini", target = "fechaInicio")
    @Mapping(source = "fechafin", target = "fechaFin")
    @Mapping(source = "paciente.id", target = "pacienteId")
    @Mapping(source = "nutricionista.id", target = "nutricionistaId")
    @Mapping(target = "paciente", ignore = true) // Handle separately if needed
    @Mapping(target = "nutricionista", ignore = true) // Handle separately if needed
    CitaDTO toDto(Cita cita);

    @Mapping(source = "fechaInicio", target = "fechaini")
    @Mapping(source = "fechaFin", target = "fechafin")
    @Mapping(source = "pacienteId", target = "paciente", qualifiedByName = "mapPacienteId")
    @Mapping(source = "nutricionistaId", target = "nutricionista", qualifiedByName = "mapNutricionistaId")
    Cita toEntity(CitaDTO citaDTO);

    
    List<CitaDTO> toDtoList(List<Cita> citas);
    List<Cita> toEntityList(List<CitaDTO> citaDTOs);

    @Named("mapPacienteId")
    default Paciente mapPacienteId(Integer pacienteId) {
        if (pacienteId == null) {
            return null;
        }
        Paciente paciente = new Paciente();
        paciente.setId(pacienteId);
        return paciente;
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
