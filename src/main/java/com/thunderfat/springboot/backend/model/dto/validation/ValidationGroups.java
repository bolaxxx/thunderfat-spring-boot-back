package com.thunderfat.springboot.backend.model.dto.validation;

/**
 * Interfaz para definir grupos de validación
 * Permite aplicar diferentes conjuntos de validaciones según el contexto
 */
public final class ValidationGroups {
    
    private ValidationGroups() {
        // Utility class
    }
    
    /**
     * Grupo de validación para operaciones de creación
     */
    public interface Create {}
    
    /**
     * Grupo de validación para operaciones de actualización
     */
    public interface Update {}
    
    /**
     * Grupo de validación para campos básicos/obligatorios
     */
    public interface Basic {}
    
    /**
     * Grupo de validación para campos completos/detallados
     */
    public interface Complete {}
    
    /**
     * Grupo de validación para login
     */
    public interface Login {}
    
    /**
     * Grupo de validación para registro
     */
    public interface Register {}
}
