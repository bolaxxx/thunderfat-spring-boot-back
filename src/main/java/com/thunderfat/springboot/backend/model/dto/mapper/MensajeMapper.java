package com.thunderfat.springboot.backend.model.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.MensajeDTO;
import com.thunderfat.springboot.backend.model.entity.Mensaje;

@Mapper(componentModel = "spring")
public interface MensajeMapper {
    MensajeMapper INSTANCE = Mappers.getMapper(MensajeMapper.class);
    
    @Mapping(source = "id_mensaje", target = "idMensaje")
    @Mapping(source = "chat.id_chat", target = "chat.idChat")
    @Mapping(source = "chat.paciente.id", target = "chat.pacienteId")
    @Mapping(source = "chat.nutricionista.id", target = "chat.nutricionistaId")
    @Mapping(target = "chat.mensajesIds", ignore = true) // Complex collection mapping
    @Mapping(target = "emisor", ignore = true)
    MensajeDTO toDto(Mensaje mensaje);
    
    List<MensajeDTO> toDtoList(List<Mensaje> mensajes);
    
    @Mapping(source = "idMensaje", target = "id_mensaje")
    @Mapping(source = "chat.idChat", target = "chat.id_chat")
    @Mapping(target = "chat.paciente", ignore = true) // Set manually based on chat.pacienteId
    @Mapping(target = "chat.nutricionista", ignore = true) // Set manually based on chat.nutricionistaId
    @Mapping(target = "chat.mensajes", ignore = true) // Complex collection mapping
    @Mapping(target = "emisor", ignore = true)
    Mensaje toEntity(MensajeDTO mensajeDTO);
    
    List<Mensaje> toEntityList(List<MensajeDTO> mensajesDTO);
}
