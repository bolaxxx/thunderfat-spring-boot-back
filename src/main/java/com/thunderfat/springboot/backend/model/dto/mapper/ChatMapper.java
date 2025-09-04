package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.entity.Chat;
import com.thunderfat.springboot.backend.model.entity.Mensaje;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Mapper
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);
    
    @Mapping(source = "id_chat", target = "idChat")
    @Mapping(source = "chat.paciente.id", target = "pacienteId")
    @Mapping(source = "chat.nutricionista.id", target = "nutricionistaId")
    @Mapping(source = "chat.mensajes", target = "mensajesIds", qualifiedByName = "mapMensajesToIds")
    ChatDTO toDto(Chat chat);

    @Mapping(source = "idChat", target = "id_chat")
    @Mapping(source = "pacienteId", target = "paciente", qualifiedByName = "mapPacienteId")
    @Mapping(source = "nutricionistaId", target = "nutricionista", qualifiedByName = "mapNutricionistaId")
    @Mapping(target = "mensajes", ignore = true) // Messages are handled separately
    Chat toEntity(ChatDTO chatDTO);

    List<ChatDTO> toDtoList(List<Chat> chats);
    List<Chat> toEntityList(List<ChatDTO> chatDTOs);

    @Named("mapMensajesToIds")
    default List<Integer> mapMensajesToIds(List<Mensaje> mensajes) {
        return Optional.ofNullable(mensajes)
                .orElse(Collections.emptyList())
                .stream()
                .map(Mensaje::getId_mensaje)
                .collect(Collectors.toList());
    }

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
