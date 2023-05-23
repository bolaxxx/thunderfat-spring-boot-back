package com.thunderfat.springboot.backend.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.RolDTO;
import com.thunderfat.springboot.backend.model.entity.Rol;

@Mapper
public interface RolMapper {
    RolMapper INSTANCE = Mappers.getMapper(RolMapper.class);

    RolDTO toDTO(Rol rol);

    Rol toEntity(RolDTO rolDTO);
}