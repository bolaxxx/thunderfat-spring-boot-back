package com.thunderfat.springboot.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuración de propiedades para cumplimiento normativo español.
 * 
 * Centraliza todas las configuraciones necesarias para:
 * - Sistema Verifactu (obligatorio desde julio 2025)
 * - Generación de archivos Facturae B2B
 * - Gestión de certificados digitales
 * - Cálculo de IVA según normativa española
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thunderfat.spain")
public class SpanishBillingProperties {

    /**
     * Configuración de la empresa
     */
    private Company company = new Company();

    /**
     * Configuración de Verifactu
     */
    private Verifactu verifactu = new Verifactu();

    /**
     * Configuración de Facturae
     */
    private Facturae facturae = new Facturae();

    /**
     * Configuración de certificados digitales
     */
    private Certificados certificados = new Certificados();

    /**
     * Configuración de IVA
     */
    private Iva iva = new Iva();

    @Data
    public static class Company {
        /**
         * NIF de la empresa
         */
        private String nif = "B12345678";

        /**
         * Nombre de la empresa
         */
        private String nombre = "ThunderFat Nutrición S.L.";

        /**
         * Nombre comercial
         */
        private String nombreComercial = "ThunderFat";

        /**
         * Dirección fiscal
         */
        private String direccion = "Calle Principal, 123";

        /**
         * Código postal
         */
        private String codigoPostal = "28001";

        /**
         * Población
         */
        private String poblacion = "Madrid";

        /**
         * Provincia
         */
        private String provincia = "Madrid";

        /**
         * País
         */
        private String pais = "España";

        /**
         * Código de país ISO
         */
        private String codigoPais = "ES";

        /**
         * Teléfono de contacto
         */
        private String telefono = "912345678";

        /**
         * Email de contacto
         */
        private String email = "facturacion@thunderfat.com";

        /**
         * Página web
         */
        private String web = "https://www.thunderfat.com";
    }

    @Data
    public static class Verifactu {
        /**
         * URL del endpoint de Verifactu
         */
        private String endpoint = "https://prewww2.aeat.es/wlpl/TIKE-CONT/ValidarRegistrosFacturacion";

        /**
         * URL del endpoint de pruebas
         */
        private String endpointTest = "https://prewww2.aeat.es/wlpl/TIKE-CONT-PREPRO/ValidarRegistrosFacturacion";

        /**
         * Indica si está en modo de pruebas
         */
        private boolean testMode = true;

        /**
         * Timeout para las peticiones (en segundos)
         */
        private int timeoutSeconds = 30;

        /**
         * Número máximo de reintentos
         */
        private int maxRetries = 3;

        /**
         * Versión del sistema Verifactu
         */
        private String version = "1.0";

        /**
         * Sistema informático emisor
         */
        private String sistemaInformaticoEmisor = "ThunderFat Billing System v1.0";

        /**
         * Identificador del terminal utilizado
         */
        private String idTerminal = "THUNDERFAT_TERMINAL_01";
    }

    @Data
    public static class Facturae {
        /**
         * Directorio donde se almacenan los archivos Facturae
         */
        private String outputDirectory = "./facturae";

        /**
         * Versión de Facturae a utilizar
         */
        private String version = "3.2.2";

        /**
         * Esquema XSD para validación
         */
        private String schemaLocation = "http://www.facturae.es/Facturae/2014/v3.2.1/Facturae";

        /**
         * Tipo de factura por defecto
         */
        private String tipoFacturaDefecto = "FC";

        /**
         * Moneda por defecto
         */
        private String monedaDefecto = "EUR";

        /**
         * Idioma por defecto
         */
        private String idiomaDefecto = "es";

        /**
         * Indica si se debe firmar digitalmente
         */
        private boolean firmarDigitalmente = true;

        /**
         * Indica si se debe validar contra el esquema XSD
         */
        private boolean validarEsquema = true;
    }

    @Data
    public static class Certificados {
        /**
         * Ruta del keystore con los certificados
         */
        private String keystorePath = "./certificates/thunderfat.p12";

        /**
         * Contraseña del keystore
         */
        private String keystorePassword = "${KEYSTORE_PASSWORD:changeit}";

        /**
         * Tipo de keystore
         */
        private String keystoreType = "PKCS12";

        /**
         * Alias del certificado para firmar
         */
        private String certificateAlias = "thunderfat-sign";

        /**
         * Contraseña del certificado
         */
        private String certificatePassword = "${CERTIFICATE_PASSWORD:changeit}";

        /**
         * Ruta del truststore con certificados de confianza
         */
        private String truststorePath = "./certificates/truststore.jks";

        /**
         * Contraseña del truststore
         */
        private String truststorePassword = "${TRUSTSTORE_PASSWORD:changeit}";

        /**
         * Validar cadena de certificados
         */
        private boolean validarCadena = true;

        /**
         * Verificar revocación de certificados
         */
        private boolean verificarRevocacion = false;
    }

    @Data
    public static class Iva {
        /**
         * Tipo de IVA general (%)
         */
        private double tipoGeneral = 21.0;

        /**
         * Tipo de IVA reducido (%)
         */
        private double tipoReducido = 10.0;

        /**
         * Tipo de IVA superreducido (%)
         */
        private double tipoSuperreducido = 4.0;

        /**
         * Tipo de IVA para servicios médicos (%)
         */
        private double tipoMedico = 4.0;

        /**
         * Indica si aplicar IVA médico automáticamente
         */
        private boolean aplicarIvaMedicoAutomatico = true;

        /**
         * Códigos de servicios que aplican IVA médico
         */
        private String[] serviciosMedicos = {"NUTRI_MEDICO", "CONSULTA_MEDICA"};

        /**
         * Umbral para considerarse empresario (euros)
         */
        private double umbralEmpresario = 1000.0;

        /**
         * Régimen especial aplicable
         */
        private String regimenEspecial = "01"; // Régimen general
    }
}
