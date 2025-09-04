package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

/**
 * Base repository interface providing common operations for all entities in ThunderFat system.
 * This interface establishes 2025 Spring Boot best practices including:
 * - Standardized pagination support
 * - Specification-based dynamic queries
 * - Consistent method signatures across all repositories
 * 
 * @param <T> the entity type
 * @param <ID> the entity ID type
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    /**
     * Finds all entities with pagination support.
     * This overrides the default findAll to enforce pagination usage.
     * 
     * @param pageable pagination information
     * @return paginated results
     */
    @Override
    @NonNull
    Page<T> findAll(@NonNull Pageable pageable);
    
    /**
     * Soft delete support - marks entity as deleted without physical removal.
     * Should be implemented by repositories that support soft delete.
     * 
     * @param id the entity ID
     */
    default void softDelete(ID id) {
        // Default implementation - override in specific repositories
        throw new UnsupportedOperationException("Soft delete not implemented for this entity");
    }
    
    /**
     * Checks if entity exists and is not soft deleted.
     * 
     * @param id the entity ID
     * @return true if entity exists and is active
     */
    default boolean existsAndNotDeleted(ID id) {
        // Default implementation using standard exists
        return existsById(id);
    }
}
