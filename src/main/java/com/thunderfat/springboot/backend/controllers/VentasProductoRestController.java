package com.thunderfat.springboot.backend.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thunderfat.springboot.backend.model.entity.Producto;
import com.thunderfat.springboot.backend.model.service.ProductoService;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller para ventas de productos en el centro de nutrición.
 * Maneja la creación de facturas de productos y reportes de ventas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200"})
public class VentasProductoRestController {

    private final ProductoService productoService;

    /**
     * DTO para solicitud de venta de productos
     */
    @Data
    public static class VentaProductoRequest {
        private Integer idPaciente;
        private Integer idNutricionista;
        private List<ProductoVentaDto> productos = new ArrayList<>();
        private String observaciones;
    }

    /**
     * DTO para cada producto en la venta
     */
    @Data
    public static class ProductoVentaDto {
        private Integer idProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario; // Opcional
    }

    /**
     * Procesa una venta de productos (versión simplificada para demostración)
     */
    @PostMapping("/productos")
    public ResponseEntity<Map<String, Object>> procesarVentaProductos(
            @Valid @RequestBody VentaProductoRequest request) {
        
        log.info("Procesando venta de productos para paciente: {}", request.getIdPaciente());
        
        try {
            // Validar productos y stock
            BigDecimal totalVenta = BigDecimal.ZERO;
            List<Map<String, Object>> productosVendidos = new ArrayList<>();
            
            for (ProductoVentaDto productoVenta : request.getProductos()) {
                // Verificar que el producto existe
                Producto producto = productoService.findById(productoVenta.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoVenta.getIdProducto()));
                
                // Verificar stock
                if (producto.getStockActual() < productoVenta.getCantidad()) {
                    throw new RuntimeException(String.format(
                            "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                            producto.getNombre(), producto.getStockActual(), productoVenta.getCantidad()));
                }
                
                // Calcular precio (usar precio especificado o precio del producto)
                BigDecimal precioUnitario = productoVenta.getPrecioUnitario() != null 
                        ? productoVenta.getPrecioUnitario() 
                        : producto.getPrecioVenta();
                
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(productoVenta.getCantidad()));
                totalVenta = totalVenta.add(subtotal);
                
                // Descontar stock (esto sería parte de la transacción real)
                productoService.salidaStock(producto.getId(), productoVenta.getCantidad(), "Venta directa");
                
                // Agregar a la lista de productos vendidos
                Map<String, Object> productoInfo = new HashMap<>();
                productoInfo.put("id", producto.getId());
                productoInfo.put("nombre", producto.getNombre());
                productoInfo.put("categoria", producto.getCategoria().name());
                productoInfo.put("cantidad", productoVenta.getCantidad());
                productoInfo.put("precioUnitario", precioUnitario);
                productoInfo.put("subtotal", subtotal);
                productoInfo.put("stockRestante", producto.getStockActual() - productoVenta.getCantidad());
                
                productosVendidos.add(productoInfo);
            }
            
            // Preparar respuesta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("mensaje", "Venta procesada exitosamente");
            respuesta.put("idPaciente", request.getIdPaciente());
            respuesta.put("idNutricionista", request.getIdNutricionista());
            respuesta.put("totalVenta", totalVenta);
            respuesta.put("productos", productosVendidos);
            respuesta.put("observaciones", request.getObservaciones());
            respuesta.put("fechaVenta", LocalDate.now());
            
            log.info("Venta procesada exitosamente. Total: {}", totalVenta);
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            log.error("Error procesando venta: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDate.now());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Obtiene reporte de ventas de productos por período
     */
    @GetMapping("/reporte")
    public ResponseEntity<Map<String, Object>> obtenerReporteVentas(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        
        log.info("Generando reporte de ventas desde {} hasta {}", fechaInicio, fechaFin);
        
        try {
            // Este sería un reporte más complejo conectado con las facturas reales
            Map<String, Object> reporte = new HashMap<>();
            reporte.put("periodoInicio", fechaInicio);
            reporte.put("periodoFin", fechaFin);
            reporte.put("mensaje", "Funcionalidad de reporte en desarrollo");
            reporte.put("valorInventarioActual", productoService.calcularValorInventario());
            reporte.put("productosStockBajo", productoService.getProductosConStockBajo().size());
            reporte.put("productosAgotados", productoService.getProductosAgotados().size());
            
            return ResponseEntity.ok(reporte);
            
        } catch (Exception e) {
            log.error("Error generando reporte: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error generando reporte: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verifica disponibilidad de productos para una venta
     */
    @PostMapping("/verificar-disponibilidad")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @RequestBody List<ProductoVentaDto> productos) {
        
        log.info("Verificando disponibilidad para {} productos", productos.size());
        
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> productosInfo = new ArrayList<>();
        boolean todosDisponibles = true;
        BigDecimal totalEstimado = BigDecimal.ZERO;
        
        for (ProductoVentaDto prodVenta : productos) {
            Map<String, Object> info = new HashMap<>();
            
            try {
                Producto producto = productoService.findById(prodVenta.getIdProducto())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
                boolean disponible = producto.getStockActual() >= prodVenta.getCantidad();
                BigDecimal precio = prodVenta.getPrecioUnitario() != null 
                        ? prodVenta.getPrecioUnitario() 
                        : producto.getPrecioVenta();
                BigDecimal subtotal = precio.multiply(new BigDecimal(prodVenta.getCantidad()));
                
                info.put("idProducto", producto.getId());
                info.put("nombre", producto.getNombre());
                info.put("stockDisponible", producto.getStockActual());
                info.put("cantidadSolicitada", prodVenta.getCantidad());
                info.put("disponible", disponible);
                info.put("precio", precio);
                info.put("subtotal", subtotal);
                
                if (disponible) {
                    totalEstimado = totalEstimado.add(subtotal);
                } else {
                    todosDisponibles = false;
                }
                
            } catch (Exception e) {
                info.put("error", e.getMessage());
                todosDisponibles = false;
            }
            
            productosInfo.add(info);
        }
        
        resultado.put("todosDisponibles", todosDisponibles);
        resultado.put("totalEstimado", totalEstimado);
        resultado.put("productos", productosInfo);
        resultado.put("fecha", LocalDate.now());
        
        return ResponseEntity.ok(resultado);
    }
}
