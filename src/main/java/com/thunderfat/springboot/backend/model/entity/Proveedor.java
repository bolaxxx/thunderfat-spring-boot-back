package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un proveedor de productos para el centro de nutrición.
 * Incluye información comercial, fiscal y de contacto.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
@Entity
@Table(name = "proveedores", indexes = {
    @Index(name = "idx_proveedor_nif", columnList = "nif"),
    @Index(name = "idx_proveedor_activo", columnList = "activo"),
    @Index(name = "idx_proveedor_nombre", columnList = "nombreComercial")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * NIF/CIF del proveedor
     */
    @Column(name = "nif", nullable = false, unique = true, length = 20)
    private String nif;

    /**
     * Razón social del proveedor
     */
    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    /**
     * Nombre comercial
     */
    @Column(name = "nombre_comercial", nullable = false, length = 200)
    private String nombreComercial;

    /**
     * Dirección fiscal
     */
    @Column(name = "direccion", nullable = false, length = 300)
    private String direccion;

    /**
     * Código postal
     */
    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    /**
     * Población
     */
    @Column(name = "poblacion", length = 100)
    private String poblacion;

    /**
     * Provincia
     */
    @Column(name = "provincia", length = 100)
    private String provincia;

    /**
     * País
     */
    @Column(name = "pais", length = 100)
    private String pais = "España";

    /**
     * Teléfono de contacto
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Email de contacto
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Página web
     */
    @Column(name = "web", length = 200)
    private String web;

    /**
     * Persona de contacto
     */
    @Column(name = "contacto_nombre", length = 100)
    private String contactoNombre;

    /**
     * Teléfono de la persona de contacto
     */
    @Column(name = "contacto_telefono", length = 20)
    private String contactoTelefono;

    /**
     * Email de la persona de contacto
     */
    @Column(name = "contacto_email", length = 100)
    private String contactoEmail;

    /**
     * Tipo de proveedor
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proveedor", nullable = false)
    private TipoProveedor tipoProveedor;

    /**
     * Condiciones de pago (días)
     */
    @Column(name = "condiciones_pago_dias")
    private Integer condicionesPagoDias = 30;

    /**
     * Descuento por volumen (%)
     */
    @Column(name = "descuento_volumen")
    private Double descuentoVolumen = 0.0;

    /**
     * Observaciones sobre el proveedor
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Indica si el proveedor está activo
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Fecha de alta del proveedor
     */
    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    /**
     * Última actualización
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Productos del proveedor
     */
    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
    private List<Producto> productos;

    /**
     * Tipos de proveedores según especialización
     */
    public enum TipoProveedor {
        SUPLEMENTOS_DEPORTIVOS("Suplementos Deportivos"),
        ALIMENTACION_NATURAL("Alimentación Natural"),
        ROPA_DEPORTIVA("Ropa Deportiva"),
        EQUIPAMIENTO_DEPORTIVO("Equipamiento Deportivo"),
        EDITORIAL_LIBROS("Editorial de Libros"),
        TECNOLOGIA_SALUD("Tecnología para la Salud"),
        COSMETICA_NATURAL("Cosmética Natural"),
        UTENSILIOS_COCINA("Utensilios de Cocina"),
        MAYORISTA_GENERAL("Mayorista General"),
        FABRICANTE_DIRECTO("Fabricante Directo"),
        DISTRIBUIDOR_OFICIAL("Distribuidor Oficial"),
        OTROS("Otros");

        private final String descripcion;

        TipoProveedor(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
