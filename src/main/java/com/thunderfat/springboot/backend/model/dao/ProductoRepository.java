package com.thunderfat.springboot.backend.model.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thunderfat.springboot.backend.model.entity.Producto;

/**
 * Repositorio para gestión de productos vendibles en el centro de nutrición.
 * 
 * Proporciona métodos especializados para búsquedas por categoría,
 * gestión de stock, productos destacados y análisis de ventas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
public interface ProductoRepository extends BaseRepository<Producto, Integer> {

    /**
     * Busca un producto por su código único
     * 
     * @param codigoProducto Código del producto (SKU)
     * @return Producto encontrado
     */
    Optional<Producto> findByCodigoProducto(String codigoProducto);

    /**
     * Busca un producto por código de barras
     * 
     * @param codigoBarras Código de barras del producto
     * @return Producto encontrado
     */
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    /**
     * Lista productos activos con paginación
     * 
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos
     */
    Page<Producto> findByActivoOrderByNombreAsc(Boolean activo, Pageable pageable);

    /**
     * Lista productos por categoría
     * 
     * @param categoria Categoría del producto
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos
     */
    Page<Producto> findByCategoriaAndActivoOrderByNombreAsc(
            Producto.CategoriaProducto categoria, Boolean activo, Pageable pageable);

    /**
     * Lista productos por subcategoría
     * 
     * @param subcategoria Subcategoría del producto
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos
     */
    Page<Producto> findBySubcategoriaAndActivoOrderByNombreAsc(
            Producto.SubcategoriaProducto subcategoria, Boolean activo, Pageable pageable);

    /**
     * Lista productos destacados
     * 
     * @param destacado Productos destacados
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos destacados
     */
    Page<Producto> findByDestacadoAndActivoOrderByNombreAsc(Boolean destacado, Boolean activo, Pageable pageable);

    /**
     * Busca productos por nombre o descripción
     * 
     * @param termino Término de búsqueda
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos que coinciden
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = :activo AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "ORDER BY p.nombre ASC")
    Page<Producto> buscarPorTermino(@Param("termino") String termino, 
                                   @Param("activo") Boolean activo, 
                                   Pageable pageable);

    /**
     * Lista productos con stock bajo
     * 
     * @return Lista de productos con stock por debajo del mínimo
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual <= p.stockMinimo ORDER BY p.stockActual ASC")
    List<Producto> findProductosConStockBajo();

    /**
     * Lista productos sin stock
     * 
     * @return Lista de productos agotados
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stockActual = 0 ORDER BY p.nombre ASC")
    List<Producto> findProductosAgotados();

    /**
     * Lista productos por proveedor
     * 
     * @param proveedorId ID del proveedor
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos del proveedor
     */
    Page<Producto> findByProveedorIdAndActivoOrderByNombreAsc(Integer proveedorId, Boolean activo, Pageable pageable);

    /**
     * Lista productos en rango de precios
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos en el rango de precios
     */
    Page<Producto> findByPrecioVentaBetweenAndActivoOrderByPrecioVentaAsc(
            BigDecimal precioMin, BigDecimal precioMax, Boolean activo, Pageable pageable);

    /**
     * Cuenta productos por categoría
     * 
     * @param categoria Categoría del producto
     * @param activo Estado del producto
     * @return Número de productos en la categoría
     */
    long countByCategoriaAndActivo(Producto.CategoriaProducto categoria, Boolean activo);

    /**
     * Productos más vendidos en un período
     * 
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @param limit Límite de resultados
     * @return Lista de arrays con [producto, cantidad_vendida, importe_total]
     */
    @Query("SELECT lf.producto, SUM(lf.cantidad), SUM(lf.totalLinea) " +
           "FROM LineaFactura lf JOIN lf.factura f " +
           "WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA' AND lf.tipoLinea = 'PRODUCTO' " +
           "GROUP BY lf.producto " +
           "ORDER BY SUM(lf.cantidad) DESC")
    List<Object[]> findProductosMasVendidos(
            @Param("fechaInicio") java.time.LocalDate fechaInicio,
            @Param("fechaFin") java.time.LocalDate fechaFin,
            Pageable pageable);

    /**
     * Calcula el valor total del stock
     * 
     * @return Valor total del inventario
     */
    @Query("SELECT SUM(p.stockActual * p.precioCoste) FROM Producto p WHERE p.activo = true")
    BigDecimal calcularValorTotalStock();

    /**
     * Productos que requieren reposición
     * 
     * @return Lista de productos que necesitan reposición
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "p.stockActual < (p.stockMinimo * 1.2) ORDER BY p.stockActual ASC")
    List<Producto> findProductosParaReposicion();

    /**
     * Lista productos por marca
     * 
     * @param marca Marca del producto
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos de la marca
     */
    Page<Producto> findByMarcaIgnoreCaseAndActivoOrderByNombreAsc(String marca, Boolean activo, Pageable pageable);

    /**
     * Productos aptos para veganos
     * 
     * @param aptoVegano Apto para veganos
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos veganos
     */
    Page<Producto> findByAptoVeganoAndActivoOrderByNombreAsc(Boolean aptoVegano, Boolean activo, Pageable pageable);

    /**
     * Productos sin gluten
     * 
     * @param sinGluten Sin gluten
     * @param activo Estado del producto
     * @param pageable Configuración de paginación
     * @return Página de productos sin gluten
     */
    Page<Producto> findBySinGlutenAndActivoOrderByNombreAsc(Boolean sinGluten, Boolean activo, Pageable pageable);

    /**
     * Obtiene todas las marcas disponibles
     * 
     * @return Lista de marcas únicas
     */
    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.activo = true AND p.marca IS NOT NULL ORDER BY p.marca ASC")
    List<String> findAllMarcas();

    /**
     * Productos con descuento aplicable
     * 
     * @param precioVentaMinimo Precio mínimo para descuento
     * @param activo Estado del producto
     * @return Lista de productos elegibles para descuento
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = :activo AND p.precioVenta >= :precioVentaMinimo ORDER BY p.precioVenta DESC")
    List<Producto> findProductosParaDescuento(@Param("precioVentaMinimo") BigDecimal precioVentaMinimo, 
                                            @Param("activo") Boolean activo);
}
