package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thunderfat.springboot.backend.model.entity.Factura;

/**
 * Repositorio para gestión de facturas con cumplimiento normativo español.
 * 
 * Proporciona métodos especializados para consultas de facturación,
 * incluyendo búsquedas por criterios de negocio y agregaciones
 * necesarias para reportes de cumplimiento fiscal.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Spanish Market Compliance
 */
public interface FacturaRepository extends BaseRepository<Factura, Integer> {

    /**
     * Busca una factura por su número único
     * 
     * @param numeroFactura Número de factura a buscar
     * @return Factura si existe
     */
    Optional<Factura> findByNumeroFactura(String numeroFactura);

    /**
     * Lista facturas de un nutricionista específico
     * 
     * @param nutricionistaId ID del nutricionista
     * @param pageable Configuración de paginación
     * @return Página de facturas
     */
    @Query("SELECT f FROM Factura f WHERE f.nutricionista.id = :nutricionistaId ORDER BY f.fechaEmision DESC")
    Page<Factura> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);

    /**
     * Lista facturas de un paciente específico
     * 
     * @param pacienteId ID del paciente
     * @param pageable Configuración de paginación
     * @return Página de facturas
     */
    @Query("SELECT f FROM Factura f WHERE f.paciente.id = :pacienteId ORDER BY f.fechaEmision DESC")
    Page<Factura> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);

    /**
     * Cuenta facturas emitidas en un año específico
     * Utilizado para generar numeración secuencial anual
     * 
     * @param año Año a consultar
     * @return Número de facturas del año
     */
    @Query("SELECT COUNT(f) FROM Factura f WHERE YEAR(f.fechaEmision) = :ano")
    long countByFechaEmisionYear(@Param("ano") int año);

    /**
     * Busca facturas pendientes de un nutricionista
     * 
     * @param nutricionistaId ID del nutricionista
     * @return Lista de facturas pendientes
     */
    @Query("SELECT f FROM Factura f WHERE f.nutricionista.id = :nutricionistaId " +
           "AND f.estado = 'PENDIENTE' ORDER BY f.fechaVencimiento ASC")
    Page<Factura> findPendientesByNutricionista(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);

    /**
     * Busca facturas vencidas
     * 
     * @param fechaActual Fecha actual para comparar vencimientos
     * @return Lista de facturas vencidas
     */
    @Query("SELECT f FROM Factura f WHERE f.fechaVencimiento < :fechaActual " +
           "AND f.estado = 'PENDIENTE' ORDER BY f.fechaVencimiento ASC")
    Page<Factura> findVencidas(@Param("fechaActual") LocalDate fechaActual, Pageable pageable);

    /**
     * Busca facturas por rango de fechas
     * 
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @param pageable Configuración de paginación
     * @return Página de facturas en el período
     */
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY f.fechaEmision DESC")
    Page<Factura> findByFechaEmisionBetween(
            @Param("fechaInicio") LocalDate fechaInicio, 
            @Param("fechaFin") LocalDate fechaFin, 
            Pageable pageable);

    /**
     * Busca facturas que requieren registro en Verifactu
     * 
     * @return Lista de facturas sin registrar en Verifactu
     */
    @Query("SELECT f FROM Factura f WHERE f.numeroRegistroVerifactu IS NULL " +
           "AND f.estado != 'ANULADA' ORDER BY f.fechaEmision ASC")
    Page<Factura> findPendientesVerifactu(Pageable pageable);

    /**
     * Busca facturas que requieren generar archivo Facturae
     * 
     * @return Lista de facturas sin archivo Facturae generado
     */
    @Query("SELECT f FROM Factura f WHERE f.requiereFacturae = true " +
           "AND f.rutaFacturaeXml IS NULL AND f.estado != 'ANULADA' " +
           "ORDER BY f.fechaEmision ASC")
    Page<Factura> findPendientesFacturae(Pageable pageable);

    /**
     * Calcula estadísticas de facturación por período
     * 
     * @param nutricionistaId ID del nutricionista (opcional)
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @return Array con [count, sum, avg] de las facturas
     */
    @Query("SELECT COUNT(f), COALESCE(SUM(f.total), 0), COALESCE(AVG(f.total), 0) " +
           "FROM Factura f WHERE (:nutricionistaId IS NULL OR f.nutricionista.id = :nutricionistaId) " +
           "AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA'")
    Object[] calcularEstadisticasPeriodo(
            @Param("nutricionistaId") Integer nutricionistaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca facturas por número de registro Verifactu
     * 
     * @param numeroRegistro Número de registro en Verifactu
     * @return Factura con ese registro
     */
    Optional<Factura> findByNumeroRegistroVerifactu(String numeroRegistro);

    /**
     * Busca la última factura emitida por un nutricionista
     * Útil para generar numeración correlativa
     * 
     * @param nutricionistaId ID del nutricionista
     * @return Última factura emitida
     */
    @Query("SELECT f FROM Factura f WHERE f.nutricionista.id = :nutricionistaId " +
           "ORDER BY f.fechaEmision DESC, f.id DESC")
    Optional<Factura> findUltimaFactura(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Cuenta facturas emitidas en un período específico
     * 
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Número de facturas emitidas
     */
    @Query("SELECT COUNT(f) FROM Factura f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND f.estado != 'ANULADA'")
    long countByPeriodo(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    /**
     * Busca facturas por estado específico
     * 
     * @param estado Estado de la factura
     * @param pageable Configuración de paginación
     * @return Página de facturas con ese estado
     */
    @Query("SELECT f FROM Factura f WHERE f.estado = :estado ORDER BY f.fechaEmision DESC")
    Page<Factura> findByEstado(@Param("estado") Factura.EstadoFactura estado, Pageable pageable);
}
