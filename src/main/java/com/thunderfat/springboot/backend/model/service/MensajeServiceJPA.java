package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.MensajeRepository;
import com.thunderfat.springboot.backend.model.dto.MensajeDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.MensajeMapper;
import com.thunderfat.springboot.backend.model.entity.Mensaje;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensajeServiceJPA implements IMensajeService {

    private final MensajeRepository repo;

    @Override
    @Transactional(readOnly = true)
    public Mensaje buscarPorId(int id_mensaje) {
        log.debug("Searching Mensaje by id: {}", id_mensaje);
        return repo.findById(id_mensaje).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mensajes-by-chat", key = "#id_chat")
    public List<Mensaje> buscarPorChat(int id_chat) {
        log.debug("Listing mensajes for chat id: {}", id_chat);
        // Derived query: findByChat_Id or custom query in repository may be needed
        try {
            return repo.findByChatId(id_chat);
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            // Fallback: load all mensajes and filter in-memory (inefficient but safe)
            log.warn("Repository lacks findByChatId method, falling back to in-memory filter");
            return repo.findAll().stream().filter(m -> m.getChat() != null && m.getChat().getId_chat() == id_chat).toList();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "mensajes-by-chat", key = "#id_mensaje")
    public void eliminarPorId(int id_mensaje) {
        log.info("Deleting mensaje with id: {}", id_mensaje);
        Optional<Mensaje> opt = repo.findById(id_mensaje);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException("Mensaje not found with id: " + id_mensaje);
        }
        repo.deleteById(id_mensaje);
    }

    @Override
    @Transactional
    @CacheEvict(value = "mensajes-by-chat", allEntries = true)
    public void eliminarPorChat(int id_chat) {
        log.info("Deleting mensajes for chat id: {}", id_chat);
        try {
            repo.deleteByChatId(id_chat);
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            log.warn("Repository lacks deleteByChatId method, falling back to manual deletion");
            List<Mensaje> mensajes = buscarPorChat(id_chat);
            repo.deleteAll(mensajes);
        }
    }

    // Convenience modern method returning DTOs
    @Transactional(readOnly = true)
    public List<MensajeDTO> listarDtoPorChat(int id_chat) {
        List<Mensaje> mensajes = buscarPorChat(id_chat);
        return MensajeMapper.INSTANCE.toDtoList(mensajes);
    }
}
