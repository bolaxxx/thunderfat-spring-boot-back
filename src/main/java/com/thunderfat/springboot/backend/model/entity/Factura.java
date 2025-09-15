package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una factura electrónica según normativa española.
 * Cumple con los requerimientos de Verifactu (obligatorio desde 1 julio 2025)
 * y Facturae B2B (requerido para 2026).
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Entity
@Table(name = "facturas", indexes = {
    @Index(name = "idx_factura_numero", columnList = "numeroFactura"),
    @Index(name = "idx_factura_fecha", columnList = "fechaEmision"),
    @Index(name = "idx_factura_verifactu", columnList = "numeroRegistroVerifactu"),
    @Index(name = "idx_factura_paciente", columnList = "paciente_id"),
    @Index(name = "idx_factura_nutricionista", columnList = "nutricionista_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Número de factura único según normativa española
     * Formato: AAAA/NNNNNNNN (Año/Número secuencial)
     */
    @Column(name = "numero_factura", nullable = false, unique = true, length = 20)
    private String numeroFactura;

    /**
     * Fecha de emisión de la factura
     */
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    /**
     * Fecha de vencimiento para el pago
     */
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    /**
     * Paciente al que se factura
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    /**
     * Nutricionista que presta el servicio
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutricionista_id", nullable = false)
    private Nutricionista nutricionista;

    /**
     * Líneas de detalle de la factura
     */
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineaFactura> lineas = new ArrayList<>();

    /**
     * Base imponible sin IVA
     */
    @Column(name = "base_imponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseImponible;

    /**
     * Tipo de IVA aplicado (4% servicios médicos, 21% estándar)
     */
    @Column(name = "tipo_iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIva;

    /**
     * Importe del IVA
     */
    @Column(name = "importe_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;

    /**
     * Total de la factura (base + IVA)
     */
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    /**
     * Estado de la factura
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    /**
     * Número de registro en Verifactu (obligatorio desde julio 2025)
     */
    @Column(name = "numero_registro_verifactu", length = 50)
    private String numeroRegistroVerifactu;

    /**
     * Hash de seguridad Verifactu
     */
    @Column(name = "hash_verifactu", length = 255)
    private String hashVerifactu;

    /**
     * Fecha de registro en Verifactu
     */
    @Column(name = "fecha_registro_verifactu")
    private LocalDateTime fechaRegistroVerifactu;

    /**
     * Indicador si la factura es B2B y requiere Facturae
     */
    @Column(name = "requiere_facturae", nullable = false)
    private Boolean requiereFacturae = false;

    /**
     * Ruta del archivo Facturae XML generado
     */
    @Column(name = "ruta_facturae_xml", length = 500)
    private String rutaFacturaeXml;

    /**
     * Certificado digital utilizado para la firma
     */
    @Column(name = "certificado_utilizado", length = 100)
    private String certificadoUtilizado;

    /**
     * Observaciones adicionales
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Datos de auditoría
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "usuario_creacion", length = 100)
    private String usuarioCreacion;

    @Column(name = "usuario_modificacion", length = 100)
    private String usuarioModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now();
        }
        if (fechaVencimiento == null) {
            fechaVencimiento = fechaEmision.plusDays(30); // 30 días por defecto
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    /**
     * Calcula el total de la factura automáticamente
     */
    public void calcularTotales() {
        if (baseImponible != null && tipoIva != null) {
            this.importeIva = baseImponible.multiply(tipoIva.divide(BigDecimal.valueOf(100)));
            this.total = baseImponible.add(importeIva);
        }
    }

    /**
     * Verifica si la factura requiere cumplimiento Verifactu
     */
    public boolean requiereVerifactu() {
        return fechaEmision.isAfter(LocalDate.of(2025, 6, 30));
    }

    /**
     * Verifica si la factura requiere formato Facturae
     */
    public boolean requiereFormatoFacturae() {
        return requiereFacturae != null && requiereFacturae && 
               fechaEmision.isAfter(LocalDate.of(2025, 12, 31));
    }

    /**
     * Estados posibles de una factura
     */
    public enum EstadoFactura {
        BORRADOR,
        PENDIENTE,
        ENVIADA,
        PAGADA,
        VENCIDA,
        ANULADA,
        REGISTRADA_VERIFACTU,
        ERROR_VERIFACTU
    }
}
