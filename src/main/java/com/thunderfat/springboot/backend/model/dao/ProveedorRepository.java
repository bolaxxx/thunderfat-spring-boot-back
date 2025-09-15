package com.thunderfat.springboot.backend.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thunderfat.springboot.backend.model.entity.Proveedor;

/**
 * Repositorio para gestión de proveedores.
 * 
 * Proporciona métodos especializados para búsquedas por tipo,
 * estado y análisis de proveedores.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
public interface ProveedorRepository extends BaseRepository<Proveedor, Integer> {

    /**
     * Busca un proveedor por NIF
     * 
     * @param nif NIF del proveedor
     * @return Proveedor encontrado
     */
    Optional<Proveedor> findByNif(String nif);

    /**
     * Lista proveedores activos
     * 
     * @param activo Estado del proveedor
     * @param pageable Configuración de paginación
     * @return Página de proveedores
     */
    Page<Proveedor> findByActivoOrderByNombreComercialAsc(Boolean activo, Pageable pageable);

    /**
     * Lista proveedores por tipo
     * 
     * @param tipoProveedor Tipo de proveedor
     * @param activo Estado del proveedor
     * @param pageable Configuración de paginación
     * @return Página de proveedores del tipo especificado
     */
    Page<Proveedor> findByTipoProveedorAndActivoOrderByNombreComercialAsc(
            Proveedor.TipoProveedor tipoProveedor, Boolean activo, Pageable pageable);

    /**
     * Busca proveedores por nombre comercial o razón social
     * 
     * @param termino Término de búsqueda
     * @param activo Estado del proveedor
     * @param pageable Configuración de paginación
     * @return Página de proveedores que coinciden
     */
    @Query("SELECT p FROM Proveedor p WHERE p.activo = :activo AND " +
           "(LOWER(p.nombreComercial) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "ORDER BY p.nombreComercial ASC")
    Page<Proveedor> buscarPorTermino(@Param("termino") String termino, 
                                   @Param("activo") Boolean activo, 
                                   Pageable pageable);

    /**
     * Lista proveedores por provincia
     * 
     * @param provincia Provincia del proveedor
     * @param activo Estado del proveedor
     * @param pageable Configuración de paginación
     * @return Página de proveedores de la provincia
     */
    Page<Proveedor> findByProvinciaIgnoreCaseAndActivoOrderByNombreComercialAsc(
            String provincia, Boolean activo, Pageable pageable);

    /**
     * Cuenta proveedores por tipo
     * 
     * @param tipoProveedor Tipo de proveedor
     * @param activo Estado del proveedor
     * @return Número de proveedores del tipo
     */
    long countByTipoProveedorAndActivo(Proveedor.TipoProveedor tipoProveedor, Boolean activo);

    /**
     * Lista todas las provincias de proveedores
     * 
     * @return Lista de provincias únicas
     */
    @Query("SELECT DISTINCT p.provincia FROM Proveedor p WHERE p.activo = true AND p.provincia IS NOT NULL ORDER BY p.provincia ASC")
    List<String> findAllProvincias();

    /**
     * Proveedores con mejores condiciones de pago
     * 
     * @param diasMaximos Máximo de días de pago
     * @param activo Estado del proveedor
     * @return Lista de proveedores con buenas condiciones
     */
    @Query("SELECT p FROM Proveedor p WHERE p.activo = :activo AND p.condicionesPagoDias <= :diasMaximos ORDER BY p.condicionesPagoDias ASC")
    List<Proveedor> findProveedoresConBuenasCondiciones(@Param("diasMaximos") Integer diasMaximos, 
                                                       @Param("activo") Boolean activo);

    /**
     * Proveedores con descuentos por volumen
     * 
     * @param descuentoMinimo Descuento mínimo requerido
     * @param activo Estado del proveedor
     * @return Lista de proveedores con descuentos
     */
    @Query("SELECT p FROM Proveedor p WHERE p.activo = :activo AND p.descuentoVolumen >= :descuentoMinimo ORDER BY p.descuentoVolumen DESC")
    List<Proveedor> findProveedoresConDescuento(@Param("descuentoMinimo") Double descuentoMinimo, 
                                               @Param("activo") Boolean activo);
}
