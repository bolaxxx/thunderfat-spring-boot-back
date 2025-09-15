package com.thunderfat.springboot.backend.spain.certificados;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestión de certificados digitales españoles.
 * Maneja certificados para firma electrónica en transacciones B2B/B2G.
 * 
 * Soporta:
 * - Certificados FNMT (Fábrica Nacional de Moneda y Timbre)
 * - Certificados de la ACCV (Agencia de Tecnología y Certificación Electrónica)
 * - Certificados de entidades certificadoras reconocidas
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Slf4j
@Service
public class CertificadoDigitalService {

    @Value("${thunderfat.spain.certificados.keystore-path}")
    private String keystorePath;

    @Value("${thunderfat.spain.certificados.keystore-password}")
    private String keystorePassword;

    @Value("${thunderfat.spain.certificados.keystore-type:PKCS12}")
    private String keystoreType;

    @Value("${thunderfat.spain.certificados.certificate-alias}")
    private String defaultAlias;

    /**
     * Cache de certificados cargados
     */
    private final Map<String, CertificateInfo> certificateCache = new ConcurrentHashMap<>();

    /**
     * KeyStore con los certificados
     */
    private KeyStore keyStore;

    /**
     * Inicializa el servicio cargando el keystore
     */
    public void init() {
        try {
            cargarKeystore();
            cargarCertificados();
            log.info("Servicio de certificados digitales inicializado correctamente");
        } catch (Exception e) {
            log.error("Error inicializando servicio de certificados: {}", e.getMessage(), e);
            throw new CertificateManagementException("Error inicializando certificados", e);
        }
    }

    /**
     * Carga el keystore desde el archivo configurado
     */
    private void cargarKeystore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        keyStore = KeyStore.getInstance(keystoreType);
        
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, keystorePassword.toCharArray());
            log.info("Keystore cargado desde: {}", keystorePath);
        }
    }

    /**
     * Carga todos los certificados del keystore en el cache
     */
    private void cargarCertificados() throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            
            if (keyStore.isKeyEntry(alias)) {
                Certificate cert = keyStore.getCertificate(alias);
                
                if (cert instanceof X509Certificate x509Cert) {
                    CertificateInfo certInfo = analizarCertificado(alias, x509Cert);
                    certificateCache.put(alias, certInfo);
                    
                    log.debug("Certificado cargado - Alias: {}, Emisor: {}, Válido hasta: {}", 
                             alias, certInfo.getIssuer(), certInfo.getNotAfter());
                }
            }
        }
        
        log.info("Cargados {} certificados en el cache", certificateCache.size());
    }

    /**
     * Analiza un certificado X.509 y extrae información relevante
     */
    private CertificateInfo analizarCertificado(String alias, X509Certificate cert) {
        CertificateInfo info = new CertificateInfo();
        info.setAlias(alias);
        info.setSubject(cert.getSubjectX500Principal().getName());
        info.setIssuer(cert.getIssuerX500Principal().getName());
        info.setSerialNumber(cert.getSerialNumber().toString());
        info.setNotBefore(cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        info.setNotAfter(cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        info.setKeyUsage(cert.getKeyUsage());
        
        // Determinar tipo de certificado español
        info.setTipoCertificado(determinarTipoCertificado(cert));
        info.setEntidadCertificadora(determinarEntidadCertificadora(cert));
        info.setValido(validarCertificado(cert));
        
        return info;
    }

    /**
     * Determina el tipo de certificado según emisor español
     */
    private TipoCertificadoEspanol determinarTipoCertificado(X509Certificate cert) {
        String issuer = cert.getIssuerX500Principal().getName().toUpperCase();
        
        if (issuer.contains("FNMT") || issuer.contains("FÁBRICA NACIONAL DE MONEDA Y TIMBRE")) {
            if (issuer.contains("PERSONA FÍSICA")) {
                return TipoCertificadoEspanol.FNMT_PERSONA_FISICA;
            } else if (issuer.contains("PERSONA JURÍDICA")) {
                return TipoCertificadoEspanol.FNMT_PERSONA_JURIDICA;
            } else {
                return TipoCertificadoEspanol.FNMT_GENERAL;
            }
        }
        
        if (issuer.contains("ACCV") || issuer.contains("AGENCIA DE TECNOLOGÍA Y CERTIFICACIÓN")) {
            return TipoCertificadoEspanol.ACCV;
        }
        
        if (issuer.contains("CAMERFIRMA")) {
            return TipoCertificadoEspanol.CAMERFIRMA;
        }
        
        if (issuer.contains("DNIE") || issuer.contains("DNI ELECTRÓNICO")) {
            return TipoCertificadoEspanol.DNIE;
        }
        
        return TipoCertificadoEspanol.OTRO;
    }

    /**
     * Determina la entidad certificadora
     */
    private String determinarEntidadCertificadora(X509Certificate cert) {
        String issuer = cert.getIssuerX500Principal().getName();
        
        if (issuer.contains("FNMT")) return "FNMT-RCM";
        if (issuer.contains("ACCV")) return "ACCV";
        if (issuer.contains("CAMERFIRMA")) return "CAMERFIRMA";
        if (issuer.contains("DNIE")) return "DGP-FNMT";
        
        return "DESCONOCIDA";
    }

    /**
     * Valida si un certificado es válido actualmente
     */
    private boolean validarCertificado(X509Certificate cert) {
        try {
            cert.checkValidity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene un certificado por su alias
     */
    public CertificateInfo obtenerCertificado(String alias) {
        CertificateInfo cert = certificateCache.get(alias);
        
        if (cert == null) {
            throw new CertificateManagementException("Certificado no encontrado: " + alias);
        }
        
        if (!cert.isValido()) {
            throw new CertificateManagementException("Certificado expirado o inválido: " + alias);
        }
        
        return cert;
    }

    /**
     * Obtiene el certificado por defecto
     */
    public CertificateInfo obtenerCertificadoDefecto() {
        return obtenerCertificado(defaultAlias);
    }

    /**
     * Lista todos los certificados disponibles
     */
    public Map<String, CertificateInfo> listarCertificados() {
        return Map.copyOf(certificateCache);
    }

    /**
     * Lista solo certificados válidos
     */
    public Map<String, CertificateInfo> listarCertificadosValidos() {
        return certificateCache.entrySet().stream()
                .filter(entry -> entry.getValue().isValido())
                .collect(ConcurrentHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        ConcurrentHashMap::putAll);
    }

    /**
     * Obtiene la clave privada de un certificado
     */
    public PrivateKey obtenerClavePrivada(String alias) {
        try {
            return (PrivateKey) keyStore.getKey(alias, keystorePassword.toCharArray());
        } catch (Exception e) {
            throw new CertificateManagementException("Error obteniendo clave privada: " + alias, e);
        }
    }

    /**
     * Verifica si un certificado es apto para firma electrónica
     */
    public boolean esAptoParaFirma(String alias) {
        CertificateInfo cert = obtenerCertificado(alias);
        
        // Verificar Key Usage para firma digital
        boolean[] keyUsage = cert.getKeyUsage();
        if (keyUsage != null && keyUsage.length > 0) {
            return keyUsage[0]; // Digital Signature
        }
        
        return false;
    }

    /**
     * Verifica si un certificado es apto para transacciones B2B
     */
    public boolean esAptoParaB2B(String alias) {
        CertificateInfo cert = obtenerCertificado(alias);
        
        // Los certificados de persona jurídica son aptos para B2B
        return cert.getTipoCertificado() == TipoCertificadoEspanol.FNMT_PERSONA_JURIDICA ||
               cert.getTipoCertificado() == TipoCertificadoEspanol.CAMERFIRMA ||
               (cert.getTipoCertificado() == TipoCertificadoEspanol.ACCV && 
                cert.getSubject().contains("PERSONA JURÍDICA"));
    }

    /**
     * Renueva el cache de certificados
     */
    public void renovarCache() {
        try {
            certificateCache.clear();
            cargarCertificados();
            log.info("Cache de certificados renovado");
        } catch (Exception e) {
            log.error("Error renovando cache de certificados: {}", e.getMessage(), e);
            throw new CertificateManagementException("Error renovando cache", e);
        }
    }

    /**
     * Información de un certificado digital
     */
    @Data
    public static class CertificateInfo {
        private String alias;
        private String subject;
        private String issuer;
        private String serialNumber;
        private LocalDateTime notBefore;
        private LocalDateTime notAfter;
        private boolean[] keyUsage;
        private TipoCertificadoEspanol tipoCertificado;
        private String entidadCertificadora;
        private boolean valido;

        public boolean estaExpirado() {
            return LocalDateTime.now().isAfter(notAfter);
        }

        public boolean estaVigente() {
            LocalDateTime now = LocalDateTime.now();
            return now.isAfter(notBefore) && now.isBefore(notAfter);
        }
    }

    /**
     * Tipos de certificados digitales españoles
     */
    public enum TipoCertificadoEspanol {
        FNMT_PERSONA_FISICA("FNMT - Persona Física"),
        FNMT_PERSONA_JURIDICA("FNMT - Persona Jurídica"),
        FNMT_GENERAL("FNMT - General"),
        ACCV("ACCV"),
        CAMERFIRMA("CamerFirma"),
        DNIE("DNI Electrónico"),
        OTRO("Otro");

        private final String descripcion;

        TipoCertificadoEspanol(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}
