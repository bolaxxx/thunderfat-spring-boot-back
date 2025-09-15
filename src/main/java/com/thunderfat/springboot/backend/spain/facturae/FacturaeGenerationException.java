package com.thunderfat.springboot.backend.spain.facturae;

/**
 * Excepción específica para errores en la generación de archivos Facturae.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
public class FacturaeGenerationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FacturaeGenerationException(String message) {
        super(message);
    }

    public FacturaeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacturaeGenerationException(Throwable cause) {
        super(cause);
    }
}
