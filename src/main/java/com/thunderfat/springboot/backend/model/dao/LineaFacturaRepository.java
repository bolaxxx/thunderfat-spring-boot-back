package com.thunderfat.springboot.backend.model.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thunderfat.springboot.backend.model.entity.LineaFactura;

/**
 * Repositorio para gestión de líneas de factura.
 * 
 * Proporciona métodos especializados para consultas de detalle
 * de facturación, incluyendo análisis de productos/servicios
 * más facturados y cálculos de IVA.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
public interface LineaFacturaRepository extends BaseRepository<LineaFactura, Integer> {

    /**
     * Busca líneas de factura por ID de factura
     * 
     * @param facturaId ID de la factura
     * @return Lista de líneas de la factura
     */
    List<LineaFactura> findByFacturaIdOrderByNumeroLinea(Integer facturaId);

    /**
     * Busca líneas de factura por código de servicio
     * 
     * @param codigoServicio Código del servicio
     * @return Lista de líneas con ese servicio
     */
    List<LineaFactura> findByCodigoServicio(String codigoServicio);

    /**
     * Calcula el total facturado por tipo de servicio en un período
     * 
     * @param codigoServicio Código del servicio
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @return Total facturado
     */
    @Query("SELECT COALESCE(SUM(lf.totalLinea), 0) FROM LineaFactura lf " +
           "JOIN lf.factura f WHERE lf.codigoServicio = :codigoServicio " +
           "AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA'")
    BigDecimal calcularTotalPorServicioPeriodo(
            @Param("codigoServicio") String codigoServicio,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Obtiene estadísticas de servicios más facturados
     * 
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @param limit Límite de resultados
     * @return Lista de arrays con [codigo_servicio, descripcion, cantidad, total]
     */
    @Query("SELECT lf.codigoServicio, lf.descripcion, COUNT(lf), SUM(lf.totalLinea) " +
           "FROM LineaFactura lf JOIN lf.factura f " +
           "WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA' " +
           "GROUP BY lf.codigoServicio, lf.descripcion " +
           "ORDER BY SUM(lf.totalLinea) DESC")
    List<Object[]> obtenerServiciosMasFacturados(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Calcula totales de IVA por tipo en un período
     * 
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @return Lista de arrays con [tipo_iva, base_imponible, importe_iva]
     */
    @Query("SELECT lf.tipoIva, SUM(lf.baseImponible), SUM(lf.importeIva) " +
           "FROM LineaFactura lf JOIN lf.factura f " +
           "WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA' " +
           "GROUP BY lf.tipoIva ORDER BY lf.tipoIva")
    List<Object[]> calcularTotalesIvaPorTipo(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca líneas de factura relacionadas con una cita específica
     * 
     * @param citaId ID de la cita
     * @return Lista de líneas relacionadas con la cita
     */
    @Query("SELECT lf FROM LineaFactura lf WHERE lf.cita.id = :citaId")
    List<LineaFactura> findByCitaId(@Param("citaId") Integer citaId);

    /**
     * Busca líneas de factura relacionadas con un plan de dieta específico
     * 
     * @param planDietaId ID del plan de dieta
     * @return Lista de líneas relacionadas con el plan
     */
    @Query("SELECT lf FROM LineaFactura lf WHERE lf.planDieta.id = :planDietaId")
    List<LineaFactura> findByPlanDietaId(@Param("planDietaId") Integer planDietaId);

    /**
     * Calcula el promedio de precio por servicio
     * 
     * @param codigoServicio Código del servicio
     * @return Precio promedio del servicio
     */
    @Query("SELECT AVG(lf.precioUnitario) FROM LineaFactura lf " +
           "JOIN lf.factura f WHERE lf.codigoServicio = :codigoServicio " +
           "AND f.estado != 'ANULADA'")
    BigDecimal calcularPrecioPromedioServicio(@Param("codigoServicio") String codigoServicio);

    /**
     * Cuenta líneas de factura por nutricionista en un período
     * 
     * @param nutricionistaId ID del nutricionista
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @return Número de líneas facturadas
     */
    @Query("SELECT COUNT(lf) FROM LineaFactura lf JOIN lf.factura f " +
           "WHERE f.nutricionista.id = :nutricionistaId " +
           "AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA'")
    long contarLineasPorNutricionistaPeriodo(
            @Param("nutricionistaId") Integer nutricionistaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
