package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.RolDTO;
import com.thunderfat.springboot.backend.model.dto.UsuarioDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.Rol;
import com.thunderfat.springboot.backend.model.entity.Usuario;

@Mapper
public interface UsuarioMapper {
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioDTO toDTO(Usuario usuario);

    Usuario toEntity(UsuarioDTO usuarioDTO);

    default RolDTO mapRol(Rol rol) {
        return RolMapper.INSTANCE.toDTO(rol);
    }

    default Rol mapRolDTO(RolDTO rolDTO) {
        return RolMapper.INSTANCE.toEntity(rolDTO);
    }

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