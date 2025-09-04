/**
 * 
 */
package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Mensaje;

/**
 * Repository for Mensaje entity. Provides derived query methods used by service layer.
 */
@Repository
public interface MensajeRepository extends BaseRepository<Mensaje, Integer> {

    /**
     * Find all mensajes for the given chat id ordered by timestamp asc.
     */
    @Query("SELECT m FROM Mensaje m WHERE m.chat.id_chat = :idChat ORDER BY m.timestamp ASC")
    List<Mensaje> findByChatId(@Param("idChat") int idChat);

    /**
     * Delete mensajes belonging to a chat.
     */
    @Query("DELETE FROM Mensaje m WHERE m.chat.id_chat = :idChat")
    void deleteByChatId(@Param("idChat") int idChat);
}
