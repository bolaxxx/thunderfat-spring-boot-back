package com.thunderfat.springboot.backend.spain.verifactu;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para consultas de registros en Verifactu.
 * Permite verificar el estado de facturas previamente registradas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifactuConsultaResponse {

    /**
     * Indica si se encontró el registro
     */
    private boolean encontrado;

    /**
     * Estado del registro en Verifactu
     */
    private String estado;

    /**
     * Fecha del registro original
     */
    private LocalDateTime fechaRegistro;

    /**
     * Fecha de última modificación
     */
    private LocalDateTime fechaModificacion;

    /**
     * Información adicional del estado
     */
    private String informacionEstado;

    /**
     * Código de error si la consulta falla
     */
    private String codigoError;

    /**
     * Mensaje de error detallado
     */
    private String mensajeError;
}
