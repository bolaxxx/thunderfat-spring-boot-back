package com.thunderfat.springboot.backend.spain.iva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para el cálculo y gestión del IVA según la normativa española.
 * Implementa las reglas específicas del mercado español para servicios de nutrición.
 * 
 * Tipos de IVA aplicables:
 * - 4% IVA super reducido: Servicios médicos y sanitarios
 * - 10% IVA reducido: Algunos productos alimentarios específicos
 * - 21% IVA general: Servicios profesionales estándar
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Slf4j
@Service
public class IvaCalculatorService {

    /**
     * Cache de tipos de IVA por código de servicio
     */
    private static final Map<String, BigDecimal> TIPOS_IVA_CACHE = new ConcurrentHashMap<>();

    /**
     * Tipos de IVA según normativa española vigente
     */
    public enum TipoIvaEspanol {
        SUPER_REDUCIDO(BigDecimal.valueOf(4.0)),    // Servicios médicos
        REDUCIDO(BigDecimal.valueOf(10.0)),         // Productos alimentarios específicos
        GENERAL(BigDecimal.valueOf(21.0));          // Servicios profesionales

        private final BigDecimal porcentaje;

        TipoIvaEspanol(BigDecimal porcentaje) {
            this.porcentaje = porcentaje;
        }

        public BigDecimal getPorcentaje() {
            return porcentaje;
        }
    }

    /**
     * Códigos de servicios médicos y sanitarios (4% IVA)
     */
    private static final String[] CODIGOS_SERVICIOS_MEDICOS = {
        "85.11", // Servicios de hospitales
        "85.12", // Servicios de medicina especializada  
        "85.13", // Servicios de medicina general
        "85.14", // Otros servicios de asistencia sanitaria
        "86.21", // Servicios de medicina general
        "86.22", // Servicios de medicina especializada
        "86.23", // Servicios de odontología
        "86.90", // Otros servicios de asistencia sanitaria
        "NUTRI_MEDICO" // Código específico para servicios de nutrición médica
    };

    /**
     * Códigos de productos alimentarios con IVA reducido (10%)
     */
    private static final String[] CODIGOS_PRODUCTOS_ALIMENTARIOS_REDUCIDO = {
        "01.1", // Cereales
        "01.2", // Hortalizas, legumbres y productos hortícolas
        "01.3", // Frutas y frutos secos
        "10.1", // Carne y productos cárnicos
        "10.2", // Pescado y productos de la pesca
        "10.3", // Leche y productos lácteos
        "SUPLEM_NUTRI" // Suplementos nutricionales específicos
    };

    /**
     * Calcula el tipo de IVA aplicable según el código de servicio/producto
     * 
     * @param codigoServicio Código del servicio según clasificación española
     * @param esServicioMedico Indica si es un servicio médico-sanitario
     * @param fechaServicio Fecha del servicio (para aplicar normativas vigentes)
     * @return Tipo de IVA aplicable
     */
    public TipoIvaEspanol calcularTipoIva(String codigoServicio, boolean esServicioMedico, LocalDate fechaServicio) {
        log.debug("Calculando tipo IVA para código: {}, servicio médico: {}, fecha: {}", 
                 codigoServicio, esServicioMedico, fechaServicio);

        // Verificar cache primero
        BigDecimal tipoCache = TIPOS_IVA_CACHE.get(codigoServicio);
        if (tipoCache != null) {
            return obtenerTipoPorPorcentaje(tipoCache);
        }

        TipoIvaEspanol tipoCalculado;

        // Servicios médicos y sanitarios - 4% IVA super reducido
        if (esServicioMedico || esCodigoServicioMedico(codigoServicio)) {
            tipoCalculado = TipoIvaEspanol.SUPER_REDUCIDO;
        }
        // Productos alimentarios específicos - 10% IVA reducido
        else if (esCodigoProductoAlimentarioReducido(codigoServicio)) {
            tipoCalculado = TipoIvaEspanol.REDUCIDO;
        }
        // Servicios profesionales estándar - 21% IVA general
        else {
            tipoCalculado = TipoIvaEspanol.GENERAL;
        }

        // Guardar en cache
        TIPOS_IVA_CACHE.put(codigoServicio, tipoCalculado.getPorcentaje());

        log.info("Tipo IVA calculado: {}% para código: {}", tipoCalculado.getPorcentaje(), codigoServicio);
        return tipoCalculado;
    }

    /**
     * Calcula el importe de IVA sobre una base imponible
     * 
     * @param baseImponible Base imponible sin IVA
     * @param tipoIva Tipo de IVA a aplicar
     * @return Importe del IVA
     */
    public BigDecimal calcularImporteIva(BigDecimal baseImponible, TipoIvaEspanol tipoIva) {
        if (baseImponible == null || tipoIva == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal importeIva = baseImponible.multiply(tipoIva.getPorcentaje())
                                           .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        
        log.debug("IVA calculado: {} sobre base {} con tipo {}%", 
                 importeIva, baseImponible, tipoIva.getPorcentaje());
        
        return importeIva;
    }

    /**
     * Calcula el total incluyendo IVA
     * 
     * @param baseImponible Base imponible sin IVA
     * @param tipoIva Tipo de IVA a aplicar
     * @return Total con IVA incluido
     */
    public BigDecimal calcularTotalConIva(BigDecimal baseImponible, TipoIvaEspanol tipoIva) {
        BigDecimal importeIva = calcularImporteIva(baseImponible, tipoIva);
        return baseImponible.add(importeIva);
    }

    /**
     * Obtiene el tipo de IVA para servicios de nutrición médica
     * 
     * @param esConsultaMedica Indica si es una consulta médica formal
     * @param tieneRecetaMedica Indica si incluye prescripción médica
     * @return Tipo de IVA aplicable
     */
    public TipoIvaEspanol obtenerTipoIvaNutricion(boolean esConsultaMedica, boolean tieneRecetaMedica) {
        // Si es consulta médica formal o incluye receta médica -> 4% IVA super reducido
        if (esConsultaMedica || tieneRecetaMedica) {
            return TipoIvaEspanol.SUPER_REDUCIDO;
        }
        // Servicios de asesoramiento nutricional estándar -> 21% IVA general
        else {
            return TipoIvaEspanol.GENERAL;
        }
    }

    /**
     * Verifica si un código corresponde a servicios médicos
     */
    private boolean esCodigoServicioMedico(String codigo) {
        if (codigo == null) return false;
        
        for (String codigoMedico : CODIGOS_SERVICIOS_MEDICOS) {
            if (codigo.startsWith(codigoMedico)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si un código corresponde a productos alimentarios con IVA reducido
     */
    private boolean esCodigoProductoAlimentarioReducido(String codigo) {
        if (codigo == null) return false;
        
        for (String codigoAlimentario : CODIGOS_PRODUCTOS_ALIMENTARIOS_REDUCIDO) {
            if (codigo.startsWith(codigoAlimentario)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el tipo de IVA por porcentaje
     */
    private TipoIvaEspanol obtenerTipoPorPorcentaje(BigDecimal porcentaje) {
        for (TipoIvaEspanol tipo : TipoIvaEspanol.values()) {
            if (tipo.getPorcentaje().compareTo(porcentaje) == 0) {
                return tipo;
            }
        }
        return TipoIvaEspanol.GENERAL; // Por defecto
    }

    /**
     * Limpia la cache de tipos de IVA
     */
    public void limpiarCache() {
        TIPOS_IVA_CACHE.clear();
        log.info("Cache de tipos IVA limpiada");
    }

    /**
     * Obtiene estadísticas del uso de tipos de IVA
     */
    public Map<String, BigDecimal> obtenerEstadisticasCache() {
        return Map.copyOf(TIPOS_IVA_CACHE);
    }
}
