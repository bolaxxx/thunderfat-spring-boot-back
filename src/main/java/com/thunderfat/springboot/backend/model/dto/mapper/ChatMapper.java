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

@Mapper(componentModel = "spring")
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);
    
    @Mapping(source = "id_chat", target = "idChat")
    @Mapping(source = "paciente.id", target = "pacienteId", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "nutricionista.id", target = "nutricionistaId", nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "mensajes", target = "mensajesIds", qualifiedByName = "mapMensajesToIds")
    ChatDTO toDto(Chat chat);

    @Mapping(source = "idChat", target = "id_chat")
    @Mapping(target = "paciente", ignore = true) // Will be set separately in service
    @Mapping(target = "nutricionista", ignore = true) // Will be set separately in service
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
}
