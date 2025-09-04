package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.UsuarioDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.Usuario;

@Mapper(uses = {RolMapper.class})
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(target = "psw", ignore = true) // Nunca incluir la contraseña en la respuesta
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "authorities", ignore = true)
    Usuario toEntity(UsuarioDTO usuarioDTO);

    /**
     * Mapeo específico para login/autenticación donde SÍ necesitamos la contraseña
     */
    @Mapping(source = "psw", target = "psw")
    @Mapping(target = "authorities", ignore = true)
    Usuario toEntityWithPassword(UsuarioDTO usuarioDTO);

    default NutricionistaDTO mapNutricionista(Nutricionista nutricionista) {
        return NutricionistaMapper.INSTANCE.toDTO(nutricionista);
    }

    default Nutricionista mapNutricionistaDTO(NutricionistaDTO nutricionistaDTO) {
        return NutricionistaMapper.INSTANCE.toEntity(nutricionistaDTO);
    }

    default PacienteDTO mapPaciente(Paciente paciente) {
        return PacienteMapper.INSTANCE.toDto(paciente);
    }

    default Paciente mapPacienteDTO(PacienteDTO pacienteDTO) {
        return PacienteMapper.INSTANCE.toEntity(pacienteDTO);
    }
}