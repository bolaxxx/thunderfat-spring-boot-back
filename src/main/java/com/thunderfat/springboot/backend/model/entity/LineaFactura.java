package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una línea de detalle en una factura.
 * Cumple con los requerimientos de facturación electrónica española.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
@Entity
@Table(name = "lineas_factura", indexes = {
    @Index(name = "idx_linea_factura", columnList = "factura_id"),
    @Index(name = "idx_linea_servicio", columnList = "codigoServicio")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaFactura implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Factura a la que pertenece esta línea
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    /**
     * Número de línea dentro de la factura
     */
    @Column(name = "numero_linea", nullable = false)
    private Integer numeroLinea;

    /**
     * Código del servicio según clasificación española
     * Para servicios de nutrición: 85.11 (Servicios de asistencia sanitaria)
     */
    @Column(name = "codigo_servicio", length = 20)
    private String codigoServicio;

    /**
     * Descripción del servicio/producto
     */
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    /**
     * Cantidad de servicios/productos
     */
    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    /**
     * Precio unitario sin IVA
     */
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * Descuento aplicado (porcentaje)
     */
    @Column(name = "descuento", precision = 5, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    /**
     * Importe sin descuento ni IVA
     */
    @Column(name = "importe_bruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeBruto;

    /**
     * Importe del descuento
     */
    @Column(name = "importe_descuento", precision = 10, scale = 2)
    private BigDecimal importeDescuento = BigDecimal.ZERO;

    /**
     * Base imponible de la línea (bruto - descuento)
     */
    @Column(name = "base_imponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseImponible;

    /**
     * Tipo de IVA aplicado a esta línea
     */
    @Column(name = "tipo_iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIva;

    /**
     * Importe del IVA para esta línea
     */
    @Column(name = "importe_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;

    /**
     * Total de la línea (base + IVA)
     */
    @Column(name = "total_linea", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLinea;

    /**
     * Cita asociada (si aplica)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    private Cita cita;

    /**
     * Plan de dieta asociado (si aplica)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_dieta_id")
    private PlanDieta planDieta;

    /**
     * Producto vendido (si aplica)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    /**
     * Tipo de línea de factura
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_linea", nullable = false)
    private TipoLineaFactura tipoLinea = TipoLineaFactura.SERVICIO;

    /**
     * Lote o número de serie del producto (si aplica)
     */
    @Column(name = "lote_producto", length = 50)
    private String loteProducto;

    /**
     * Fecha de caducidad del producto (si aplica)
     */
    @Column(name = "fecha_caducidad")
    private java.time.LocalDate fechaCaducidad;

    /**
     * Tipos de líneas de factura
     */
    public enum TipoLineaFactura {
        SERVICIO("Servicio"),
        PRODUCTO("Producto"),
        DESCUENTO("Descuento"),
        RECARGO("Recargo");

        private final String descripcion;

        TipoLineaFactura(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Calcula automáticamente los importes de la línea
     */
    public void calcularImportes() {
        // Calcular importe bruto
        this.importeBruto = cantidad.multiply(precioUnitario);
        
        // Calcular descuento
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            this.importeDescuento = importeBruto.multiply(descuento.divide(BigDecimal.valueOf(100)));
        } else {
            this.importeDescuento = BigDecimal.ZERO;
        }
        
        // Calcular base imponible
        this.baseImponible = importeBruto.subtract(importeDescuento);
        
        // Aplicar IVA según tipo de línea
        if (tipoIva == null) {
            aplicarTipoIvaEspanol();
        }
        
        // Calcular IVA
        this.importeIva = baseImponible.multiply(tipoIva.divide(BigDecimal.valueOf(100)));
        
        // Calcular total
        this.totalLinea = baseImponible.add(importeIva);
    }

    /**
     * Determina si el servicio es médico-sanitario (4% IVA)
     */
    public boolean esServicioMedico() {
        return codigoServicio != null && 
               (codigoServicio.startsWith("85.11") || 
                codigoServicio.startsWith("85.12") ||
                codigoServicio.startsWith("85.13") ||
                codigoServicio.equals("NUTRI_MEDICO"));
    }

    /**
     * Determina si es un producto alimentario (IVA reducido en algunos casos)
     */
    public boolean esProductoAlimentario() {
        return tipoLinea == TipoLineaFactura.PRODUCTO && producto != null &&
               (producto.getCategoria() == Producto.CategoriaProducto.SUPLEMENTOS_NUTRICIONALES ||
                producto.getCategoria() == Producto.CategoriaProducto.ALIMENTACION_DEPORTIVA ||
                producto.getCategoria() == Producto.CategoriaProducto.PRODUCTOS_DIETETICOS);
    }

    /**
     * Aplica el tipo de IVA según la normativa española
     */
    public void aplicarTipoIvaEspanol() {
        if (tipoLinea == TipoLineaFactura.PRODUCTO && producto != null) {
            // Para productos, usar el IVA definido en el producto
            this.tipoIva = producto.getTipoIva();
        } else if (esServicioMedico()) {
            this.tipoIva = BigDecimal.valueOf(4.0); // IVA reducido para servicios médicos
        } else {
            this.tipoIva = BigDecimal.valueOf(21.0); // IVA general
        }
    }

    /**
     * Verifica si hay stock suficiente del producto
     */
    public boolean verificarStock() {
        if (tipoLinea == TipoLineaFactura.PRODUCTO && producto != null) {
            return producto.tieneStockSuficiente(cantidad.intValue());
        }
        return true; // Para servicios siempre hay "stock"
    }

    /**
     * Reduce el stock del producto al confirmar la venta
     */
    public void procesarVenta() {
        if (tipoLinea == TipoLineaFactura.PRODUCTO && producto != null) {
            producto.reducirStock(cantidad.intValue());
        }
    }
}
