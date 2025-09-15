package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa productos vendibles en el centro de nutrición.
 * Incluye suplementos alimenticios, ropa deportiva, libros de nutrición,
 * equipamiento deportivo y otros productos relacionados.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
@Entity
@Table(name = "productos", indexes = {
    @Index(name = "idx_producto_codigo", columnList = "codigoProducto"),
    @Index(name = "idx_producto_categoria", columnList = "categoria"),
    @Index(name = "idx_producto_activo", columnList = "activo"),
    @Index(name = "idx_producto_proveedor", columnList = "proveedor_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Código único del producto (SKU)
     */
    @Column(name = "codigo_producto", nullable = false, unique = true, length = 50)
    private String codigoProducto;

    /**
     * Nombre comercial del producto
     */
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    /**
     * Descripción detallada del producto
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Marca o fabricante del producto
     */
    @Column(name = "marca", length = 100)
    private String marca;

    /**
     * Categoría del producto
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaProducto categoria;

    /**
     * Subcategoría específica
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "subcategoria")
    private SubcategoriaProducto subcategoria;

    /**
     * Precio de venta al público (sin IVA)
     */
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    /**
     * Precio de compra/coste (sin IVA)
     */
    @Column(name = "precio_coste", precision = 10, scale = 2)
    private BigDecimal precioCoste;

    /**
     * Tipo de IVA aplicable al producto
     */
    @Column(name = "tipo_iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIva;

    /**
     * Stock actual disponible
     */
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    /**
     * Stock mínimo para alertas
     */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    /**
     * Stock máximo recomendado
     */
    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    /**
     * Unidad de medida (unidades, kg, litros, etc.)
     */
    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida = "unidades";

    /**
     * Peso del producto en gramos
     */
    @Column(name = "peso_gramos")
    private Integer pesoGramos;

    /**
     * Dimensiones del producto (largo x ancho x alto en cm)
     */
    @Column(name = "dimensiones", length = 50)
    private String dimensiones;

    /**
     * Código de barras del producto
     */
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    /**
     * URL de la imagen principal del producto
     */
    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    /**
     * Información nutricional (para suplementos)
     */
    @Column(name = "informacion_nutricional", columnDefinition = "TEXT")
    private String informacionNutricional;

    /**
     * Ingredientes principales
     */
    @Column(name = "ingredientes", columnDefinition = "TEXT")
    private String ingredientes;

    /**
     * Modo de uso/instrucciones
     */
    @Column(name = "modo_uso", columnDefinition = "TEXT")
    private String modoUso;

    /**
     * Contraindicaciones o advertencias
     */
    @Column(name = "contraindicaciones", columnDefinition = "TEXT")
    private String contraindicaciones;

    /**
     * Proveedor del producto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    /**
     * Indica si el producto está activo para la venta
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Producto destacado/recomendado
     */
    @Column(name = "destacado", nullable = false)
    private Boolean destacado = false;

    /**
     * Requiere receta médica
     */
    @Column(name = "requiere_receta", nullable = false)
    private Boolean requiereReceta = false;

    /**
     * Apto para veganos
     */
    @Column(name = "apto_vegano", nullable = false)
    private Boolean aptoVegano = false;

    /**
     * Sin gluten
     */
    @Column(name = "sin_gluten", nullable = false)
    private Boolean sinGluten = false;

    /**
     * Fecha de creación del registro
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /**
     * Última actualización del registro
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Categorías principales de productos vendibles en centros de nutrición
     */
    public enum CategoriaProducto {
        SUPLEMENTOS_NUTRICIONALES("Suplementos Nutricionales"),
        ALIMENTACION_DEPORTIVA("Alimentación Deportiva"),
        ROPA_DEPORTIVA("Ropa Deportiva"),
        EQUIPAMIENTO_DEPORTIVO("Equipamiento Deportivo"),
        LIBROS_NUTRICION("Libros y Material Educativo"),
        ACCESORIOS_COCINA("Accesorios de Cocina Saludable"),
        PRODUCTOS_DIETETICOS("Productos Dietéticos"),
        COSMETICA_NATURAL("Cosmética Natural"),
        TECNOLOGIA_SALUD("Tecnología para la Salud"),
        OTROS("Otros Productos");

        private final String descripcion;

        CategoriaProducto(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Subcategorías específicas para mejor clasificación
     */
    public enum SubcategoriaProducto {
        // Suplementos Nutricionales
        PROTEINAS("Proteínas"),
        AMINOACIDOS("Aminoácidos"),
        VITAMINAS("Vitaminas y Minerales"),
        TERMOGENICOS("Termogénicos"),
        PROBIOTICOS("Probióticos"),
        OMEGA_3("Omega 3 y Ácidos Grasos"),
        CREATINA("Creatina"),
        PRE_ENTRENO("Pre-Entreno"),
        POST_ENTRENO("Post-Entreno"),
        
        // Alimentación Deportiva
        BARRITAS_ENERGETICAS("Barritas Energéticas"),
        BEBIDAS_DEPORTIVAS("Bebidas Deportivas"),
        SNACKS_SALUDABLES("Snacks Saludables"),
        COMIDA_PREPARADA("Comida Preparada Saludable"),
        
        // Ropa Deportiva
        CAMISETAS_DEPORTIVAS("Camisetas Deportivas"),
        PANTALONES_DEPORTIVOS("Pantalones Deportivos"),
        CALZADO_DEPORTIVO("Calzado Deportivo"),
        ROPA_INTERIOR_DEPORTIVA("Ropa Interior Deportiva"),
        ACCESORIOS_DEPORTIVOS("Accesorios Deportivos"),
        
        // Equipamiento Deportivo
        PESAS_MANCUERNAS("Pesas y Mancuernas"),
        BANDAS_ELASTICAS("Bandas Elásticas"),
        ESTERILLAS_YOGA("Esterillas de Yoga"),
        EQUIPOS_CARDIO("Equipos de Cardio"),
        ACCESORIOS_GIMNASIO("Accesorios de Gimnasio"),
        
        // Libros y Material
        LIBROS_NUTRICION("Libros de Nutrición"),
        GUIAS_EJERCICIO("Guías de Ejercicio"),
        RECETARIOS("Recetarios Saludables"),
        MATERIAL_EDUCATIVO("Material Educativo"),
        
        // Accesorios de Cocina
        BATIDORAS("Batidoras y Licuadoras"),
        BASCULA_COCINA("Básculas de Cocina"),
        RECIPIENTES("Recipientes y Tuppers"),
        UTENSILIOS_COCINA("Utensilios de Cocina"),
        
        // Productos Dietéticos
        PRODUCTOS_SIN_AZUCAR("Productos sin Azúcar"),
        PRODUCTOS_SIN_GLUTEN("Productos sin Gluten"),
        PRODUCTOS_VEGANOS("Productos Veganos"),
        ALIMENTOS_ORGANICOS("Alimentos Orgánicos"),
        
        // Cosmética Natural
        CREMAS_DEPORTIVAS("Cremas Deportivas"),
        PROTECTORES_SOLARES("Protectores Solares"),
        PRODUCTOS_NATURALES("Productos Naturales"),
        
        // Tecnología para la Salud
        BASCULA_CORPORAL("Básculas Corporales"),
        MONITORES_ACTIVIDAD("Monitores de Actividad"),
        PULSOMETROS("Pulsómetros"),
        APLICACIONES_MOVIL("Aplicaciones Móviles"),
        
        // Otros
        OTROS("Otros");

        private final String descripcion;

        SubcategoriaProducto(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Verifica si el producto tiene stock suficiente
     */
    public boolean tieneStockSuficiente(Integer cantidadSolicitada) {
        return stockActual >= cantidadSolicitada;
    }

    /**
     * Verifica si el stock está bajo el mínimo
     */
    public boolean stockBajo() {
        return stockActual <= stockMinimo;
    }

    /**
     * Calcula el precio con IVA
     */
    public BigDecimal getPrecioConIva() {
        return precioVenta.multiply(BigDecimal.ONE.add(tipoIva.divide(BigDecimal.valueOf(100))));
    }

    /**
     * Actualiza el stock después de una venta
     */
    public void reducirStock(Integer cantidad) {
        if (tieneStockSuficiente(cantidad)) {
            this.stockActual -= cantidad;
            this.fechaActualizacion = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + stockActual + ", Solicitado: " + cantidad);
        }
    }

    /**
     * Aumenta el stock (recepción de mercancía)
     */
    public void aumentarStock(Integer cantidad) {
        this.stockActual += cantidad;
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
