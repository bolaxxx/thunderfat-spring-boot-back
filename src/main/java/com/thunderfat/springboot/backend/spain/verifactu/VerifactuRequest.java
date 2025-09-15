package com.thunderfat.springboot.backend.spain.verifactu;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes al sistema Verifactu de la AEAT.
 * Contiene todos los datos necesarios para el registro de facturas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifactuRequest {

    /**
     * NIF del emisor de la factura
     */
    private String nifEmisor;

    /**
     * Nombre o razón social del emisor
     */
    private String nombreRazonSocial;

    /**
     * Número de factura
     */
    private String numeroFactura;

    /**
     * Fecha de emisión de la factura
     */
    private LocalDate fechaEmision;

    /**
     * Base imponible sin IVA
     */
    private BigDecimal baseImponible;

    /**
     * Tipo de IVA aplicado
     */
    private BigDecimal tipoIva;

    /**
     * Importe del IVA
     */
    private BigDecimal importeIva;

    /**
     * Total de la factura
     */
    private BigDecimal total;

    /**
     * NIF del receptor de la factura
     */
    private String nifReceptor;

    /**
     * Nombre del receptor de la factura
     */
    private String nombreReceptor;

    /**
     * Hash de seguridad SHA-256
     */
    private String hashSeguridad;

    /**
     * Indica si está en modo test
     */
    private boolean testMode;

    /**
     * Observaciones adicionales
     */
    private String observaciones;

    /**
     * Código de operación específico (por defecto: venta de servicios)
     */
    @Builder.Default
    private String codigoOperacion = "S1"; // Servicios profesionales

    /**
     * Tipo de factura según Verifactu
     */
    @Builder.Default
    private String tipoFactura = "F1"; // Factura completa

    /**
     * Moneda (por defecto EUR)
     */
    @Builder.Default
    private String moneda = "EUR";
}
