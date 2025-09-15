package com.thunderfat.springboot.backend.spain.verifactu;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas del sistema Verifactu de la AEAT.
 * Contiene el resultado del registro de facturas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifactuResponse {

    /**
     * Indica si el registro fue exitoso
     */
    private boolean exitoso;

    /**
     * Número de registro asignado por Verifactu
     */
    private String numeroRegistro;

    /**
     * Fecha y hora del registro
     */
    private LocalDateTime fechaRegistro;

    /**
     * Código de error (si aplica)
     */
    private String codigoError;

    /**
     * Mensaje de error detallado (si aplica)
     */
    private String mensajeError;

    /**
     * Hash de respuesta de Verifactu
     */
    private String hashRespuesta;

    /**
     * Código de verificación adicional
     */
    private String codigoVerificacion;

    /**
     * URL para consulta posterior del registro
     */
    private String urlConsulta;

    /**
     * Datos técnicos adicionales de la respuesta
     */
    private String datosAdicionales;
}
