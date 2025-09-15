package com.thunderfat.springboot.backend.spain.certificados;

/**
 * Excepción específica para errores en la gestión de certificados digitales.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
public class CertificateManagementException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CertificateManagementException(String message) {
        super(message);
    }

    public CertificateManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateManagementException(Throwable cause) {
        super(cause);
    }
}
