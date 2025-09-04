package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.RolDTO;
import com.thunderfat.springboot.backend.model.entity.Rol;

@Mapper
public interface RolMapper {
    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    // Entity only has id and nombre, ignore extra DTO fields
    @Mapping(target = "descripcion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    RolDTO toDTO(Rol rol);

    // Only map available fields from DTO to entity
    Rol toEntity(RolDTO rolDTO);
}