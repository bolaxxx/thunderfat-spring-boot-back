package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.dto.RolDTO;

public interface IRolService {
    List<RolDTO> listar();
    RolDTO buscarPorId(int id);
    void insertar(RolDTO rolDTO);
    void eliminar(int id);
}
