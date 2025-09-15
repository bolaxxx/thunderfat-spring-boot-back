package com.thunderfat.springboot.backend.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thunderfat.springboot.backend.model.entity.Producto;
import com.thunderfat.springboot.backend.model.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller para gestión de productos vendibles en el centro de nutrición.
 * Maneja operaciones CRUD, consultas de inventario y reportes de ventas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200"})
public class ProductoRestController {

    private final ProductoService productoService;

    /**
     * Obtiene todos los productos activos con paginación
     */
    @GetMapping
    public ResponseEntity<Page<Producto>> listarProductos(Pageable pageable) {
        log.info("Listando productos activos con paginación: {}", pageable);
        Page<Producto> productos = productoService.findProductosActivos(pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene un producto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        log.info("Obteniendo producto con ID: {}", id);
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo producto
     */
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        Producto productoCreado = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoCreado);
    }

    /**
     * Actualiza un producto existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Integer id, 
            @Valid @RequestBody Producto producto) {
        log.info("Actualizando producto con ID: {}", id);
        Producto productoActualizado = productoService.actualizarProducto(id, producto);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Busca productos por categoría
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<Producto>> buscarPorCategoria(
            @PathVariable Producto.CategoriaProducto categoria,
            Pageable pageable) {
        log.info("Buscando productos por categoría: {}", categoria);
        Page<Producto> productos = productoService.findByCategoria(categoria, pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos destacados
     */
    @GetMapping("/destacados")
    public ResponseEntity<Page<Producto>> productosDestacados(Pageable pageable) {
        log.info("Obteniendo productos destacados");
        Page<Producto> productos = productoService.findProductosDestacados(pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos con stock bajo
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> productosStockBajo() {
        log.info("Obteniendo productos con stock bajo");
        List<Producto> productos = productoService.getProductosConStockBajo();
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca productos agotados
     */
    @GetMapping("/agotados")
    public ResponseEntity<List<Producto>> productosAgotados() {
        log.info("Obteniendo productos agotados");
        List<Producto> productos = productoService.getProductosAgotados();
        return ResponseEntity.ok(productos);
    }

    /**
     * Registra entrada de stock
     */
    @PostMapping("/{id}/entrada-stock")
    public ResponseEntity<Producto> entradaStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String lote) {
        log.info("Registrando entrada de stock para producto {}: {} unidades", id, cantidad);
        Producto producto = productoService.entradaStock(id, cantidad, lote);
        return ResponseEntity.ok(producto);
    }

    /**
     * Registra salida de stock
     */
    @PostMapping("/{id}/salida-stock")
    public ResponseEntity<Producto> salidaStock(
            @PathVariable Integer id,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String motivo) {
        log.info("Registrando salida de stock para producto {}: {} unidades", id, cantidad);
        Producto producto = productoService.salidaStock(id, cantidad, motivo);
        return ResponseEntity.ok(producto);
    }

    /**
     * Busca productos por término
     */
    @GetMapping("/buscar")
    public ResponseEntity<Page<Producto>> buscarProductos(
            @RequestParam String termino,
            Pageable pageable) {
        log.info("Buscando productos con término: {}", termino);
        Page<Producto> productos = productoService.buscarProductos(termino, pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Busca producto por código
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigo) {
        log.info("Buscando producto por código: {}", codigo);
        return productoService.findByCodigoProducto(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene productos más vendidos
     */
    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<Object[]>> productosMasVendidos(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            Pageable pageable) {
        log.info("Obteniendo productos más vendidos desde {} hasta {}", fechaInicio, fechaFin);
        List<Object[]> productos = productoService.getProductosMasVendidos(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Cambia el estado de un producto
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Producto> cambiarEstadoProducto(
            @PathVariable Integer id,
            @RequestParam Boolean activo) {
        log.info("Cambiando estado del producto {} a {}", id, activo ? "activo" : "inactivo");
        Producto producto = productoService.cambiarEstadoProducto(id, activo);
        return ResponseEntity.ok(producto);
    }

    /**
     * Obtiene el valor total del inventario
     */
    @GetMapping("/valor-inventario")
    public ResponseEntity<Map<String, Object>> valorInventario() {
        log.info("Calculando valor total del inventario");
        BigDecimal valor = productoService.calcularValorInventario();
        
        Map<String, Object> response = new HashMap<>();
        response.put("valorTotal", valor);
        response.put("fecha", LocalDate.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas básicas de productos
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de productos");
        
        List<Producto> stockBajo = productoService.getProductosConStockBajo();
        List<Producto> agotados = productoService.getProductosAgotados();
        BigDecimal valorInventario = productoService.calcularValorInventario();
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("productosStockBajo", stockBajo.size());
        estadisticas.put("productosAgotados", agotados.size());
        estadisticas.put("valorInventario", valorInventario);
        estadisticas.put("fecha", LocalDate.now());
        
        return ResponseEntity.ok(estadisticas);
    }
}
