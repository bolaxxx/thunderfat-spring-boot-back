package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.RolRepository;
import com.thunderfat.springboot.backend.model.dto.RolDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.RolMapper;

@Service
public class RolServiceJPA implements IRolService {
    
    @Autowired
    private RolRepository rolRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listar() {
        return rolRepository.findAll().stream()
                .map(RolMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO buscarPorId(int id) {
        return rolRepository.findById(id)
                .map(RolMapper.INSTANCE::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public void insertar(RolDTO rolDTO) {
        rolRepository.save(RolMapper.INSTANCE.toEntity(rolDTO));
    }

    @Override
    @Transactional
    public void eliminar(int id) {
        rolRepository.deleteById(id);
    }
}
