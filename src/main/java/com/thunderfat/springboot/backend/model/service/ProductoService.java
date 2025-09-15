package com.thunderfat.springboot.backend.model.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.ProductoRepository;
import com.thunderfat.springboot.backend.model.entity.Producto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestión de productos vendibles en el centro de nutrición.
 * 
 * Proporciona funcionalidades completas para la gestión del catálogo
 * de productos, control de inventario y análisis de ventas.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4 - Product Sales Extension
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Busca un producto por su ID
     * 
     * @param id ID del producto
     * @return Producto encontrado
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos", key = "#id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Optional<Producto> findById(Integer id) {
        log.debug("Buscando producto por ID: {}", id);
        return productoRepository.findById(id);
    }

    /**
     * Busca un producto por su código
     * 
     * @param codigoProducto Código del producto (SKU)
     * @return Producto encontrado
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-codigo", key = "#codigoProducto")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Optional<Producto> findByCodigoProducto(String codigoProducto) {
        log.debug("Buscando producto por código: {}", codigoProducto);
        return productoRepository.findByCodigoProducto(codigoProducto);
    }

    /**
     * Lista productos activos con paginación
     * 
     * @param pageable Configuración de paginación
     * @return Página de productos activos
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-activos", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Producto> findProductosActivos(Pageable pageable) {
        log.debug("Listando productos activos - página: {}", pageable.getPageNumber());
        return productoRepository.findByActivoOrderByNombreAsc(true, pageable);
    }

    /**
     * Lista productos por categoría
     * 
     * @param categoria Categoría del producto
     * @param pageable Configuración de paginación
     * @return Página de productos de la categoría
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-categoria", key = "#categoria + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Producto> findByCategoria(Producto.CategoriaProducto categoria, Pageable pageable) {
        log.debug("Listando productos por categoría: {} - página: {}", categoria, pageable.getPageNumber());
        return productoRepository.findByCategoriaAndActivoOrderByNombreAsc(categoria, true, pageable);
    }

    /**
     * Lista productos destacados
     * 
     * @param pageable Configuración de paginación
     * @return Página de productos destacados
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-destacados", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Producto> findProductosDestacados(Pageable pageable) {
        log.debug("Listando productos destacados - página: {}", pageable.getPageNumber());
        return productoRepository.findByDestacadoAndActivoOrderByNombreAsc(true, true, pageable);
    }

    /**
     * Busca productos por término
     * 
     * @param termino Término de búsqueda
     * @param pageable Configuración de paginación
     * @return Página de productos que coinciden
     */
    @Transactional(readOnly = true)
    public Page<Producto> buscarProductos(String termino, Pageable pageable) {
        log.debug("Buscando productos por término: {} - página: {}", termino, pageable.getPageNumber());
        return productoRepository.buscarPorTermino(termino, true, pageable);
    }

    /**
     * Crea un nuevo producto
     * 
     * @param producto Producto a crear
     * @return Producto creado
     */
    @Transactional
    @CacheEvict(value = {"productos", "productos-activos", "productos-categoria", "productos-destacados"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Producto crearProducto(Producto producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());

        // Validaciones
        validarProducto(producto);
        
        // Verificar que el código no exista
        if (productoRepository.findByCodigoProducto(producto.getCodigoProducto()).isPresent()) {
            throw new BusinessException("Ya existe un producto con el código: " + producto.getCodigoProducto());
        }

        // Calcular tipo de IVA automáticamente si no está definido
        if (producto.getTipoIva() == null) {
            producto.setTipoIva(calcularTipoIvaProducto(producto));
        }

        Producto productorGuardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente: {} - ID: {}", productorGuardado.getNombre(), productorGuardado.getId());
        
        return productorGuardado;
    }

    /**
     * Actualiza un producto existente
     * 
     * @param id ID del producto
     * @param productoActualizado Datos actualizados
     * @return Producto actualizado
     */
    @Transactional
    @CacheEvict(value = {"productos", "productos-activos", "productos-categoria", "productos-destacados"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Producto actualizarProducto(Integer id, Producto productoActualizado) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

        // Validaciones
        validarProducto(productoActualizado);

        // Verificar código único (si cambió)
        if (!producto.getCodigoProducto().equals(productoActualizado.getCodigoProducto())) {
            if (productoRepository.findByCodigoProducto(productoActualizado.getCodigoProducto()).isPresent()) {
                throw new BusinessException("Ya existe un producto con el código: " + productoActualizado.getCodigoProducto());
            }
        }

        // Actualizar campos
        producto.setCodigoProducto(productoActualizado.getCodigoProducto());
        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setMarca(productoActualizado.getMarca());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setSubcategoria(productoActualizado.getSubcategoria());
        producto.setPrecioVenta(productoActualizado.getPrecioVenta());
        producto.setPrecioCoste(productoActualizado.getPrecioCoste());
        producto.setTipoIva(productoActualizado.getTipoIva());
        producto.setStockMinimo(productoActualizado.getStockMinimo());
        producto.setStockMaximo(productoActualizado.getStockMaximo());
        producto.setUnidadMedida(productoActualizado.getUnidadMedida());
        producto.setPesoGramos(productoActualizado.getPesoGramos());
        producto.setDimensiones(productoActualizado.getDimensiones());
        producto.setCodigoBarras(productoActualizado.getCodigoBarras());
        producto.setImagenUrl(productoActualizado.getImagenUrl());
        producto.setInformacionNutricional(productoActualizado.getInformacionNutricional());
        producto.setIngredientes(productoActualizado.getIngredientes());
        producto.setModoUso(productoActualizado.getModoUso());
        producto.setContraindicaciones(productoActualizado.getContraindicaciones());
        producto.setProveedor(productoActualizado.getProveedor());
        producto.setActivo(productoActualizado.getActivo());
        producto.setDestacado(productoActualizado.getDestacado());
        producto.setRequiereReceta(productoActualizado.getRequiereReceta());
        producto.setAptoVegano(productoActualizado.getAptoVegano());
        producto.setSinGluten(productoActualizado.getSinGluten());

        Producto productorGuardado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente: {}", productorGuardado.getNombre());
        
        return productorGuardado;
    }

    /**
     * Gestión de stock - Entrada de mercancía
     * 
     * @param id ID del producto
     * @param cantidad Cantidad a añadir al stock
     * @param lote Lote de la mercancía (opcional)
     * @return Producto actualizado
     */
    @Transactional
    @CacheEvict(value = {"productos"}, key = "#id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Producto entradaStock(Integer id, Integer cantidad, String lote) {
        log.info("Entrada de stock - Producto ID: {}, Cantidad: {}", id, cantidad);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que 0");
        }

        producto.aumentarStock(cantidad);
        Producto productorGuardado = productoRepository.save(producto);
        
        log.info("Stock actualizado - Producto: {}, Stock anterior: {}, Stock actual: {}", 
                 producto.getNombre(), producto.getStockActual() - cantidad, producto.getStockActual());
        
        return productorGuardado;
    }

    /**
     * Gestión de stock - Salida manual de mercancía
     * 
     * @param id ID del producto
     * @param cantidad Cantidad a restar del stock
     * @param motivo Motivo de la salida
     * @return Producto actualizado
     */
    @Transactional
    @CacheEvict(value = {"productos"}, key = "#id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Producto salidaStock(Integer id, Integer cantidad, String motivo) {
        log.info("Salida de stock - Producto ID: {}, Cantidad: {}, Motivo: {}", id, cantidad, motivo);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

        if (cantidad <= 0) {
            throw new BusinessException("La cantidad debe ser mayor que 0");
        }

        if (!producto.tieneStockSuficiente(cantidad)) {
            throw new BusinessException(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                                                    producto.getStockActual(), cantidad));
        }

        producto.reducirStock(cantidad);
        Producto productorGuardado = productoRepository.save(producto);
        
        log.info("Stock reducido - Producto: {}, Stock anterior: {}, Stock actual: {}", 
                 producto.getNombre(), producto.getStockActual() + cantidad, producto.getStockActual());
        
        return productorGuardado;
    }

    /**
     * Lista productos con stock bajo
     * 
     * @return Lista de productos que necesitan reposición
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-stock-bajo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public List<Producto> getProductosConStockBajo() {
        log.debug("Listando productos con stock bajo");
        return productoRepository.findProductosConStockBajo();
    }

    /**
     * Lista productos agotados
     * 
     * @return Lista de productos sin stock
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "productos-agotados")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public List<Producto> getProductosAgotados() {
        log.debug("Listando productos agotados");
        return productoRepository.findProductosAgotados();
    }

    /**
     * Activa o desactiva un producto
     * 
     * @param id ID del producto
     * @param activo Estado deseado
     * @return Producto actualizado
     */
    @Transactional
    @CacheEvict(value = {"productos", "productos-activos"}, allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Producto cambiarEstadoProducto(Integer id, Boolean activo) {
        log.info("Cambiando estado del producto ID: {} a {}", id, activo ? "activo" : "inactivo");

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

        producto.setActivo(activo);
        Producto productorGuardado = productoRepository.save(producto);
        
        log.info("Estado del producto cambiado: {} - {}", producto.getNombre(), activo ? "ACTIVO" : "INACTIVO");
        
        return productorGuardado;
    }

    /**
     * Productos más vendidos
     * 
     * @param fechaInicio Fecha inicial del período
     * @param fechaFin Fecha final del período
     * @param pageable Configuración de paginación
     * @return Lista de productos más vendidos
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public List<Object[]> getProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        log.debug("Obteniendo productos más vendidos entre {} y {}", fechaInicio, fechaFin);
        return productoRepository.findProductosMasVendidos(fechaInicio, fechaFin, pageable);
    }

    /**
     * Calcula el valor total del inventario
     * 
     * @return Valor total del stock
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "valor-inventario")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public BigDecimal calcularValorInventario() {
        log.debug("Calculando valor total del inventario");
        BigDecimal valor = productoRepository.calcularValorTotalStock();
        return valor != null ? valor : BigDecimal.ZERO;
    }

    /**
     * Validaciones de producto
     */
    private void validarProducto(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new BusinessException("El nombre del producto es obligatorio");
        }
        
        if (producto.getCodigoProducto() == null || producto.getCodigoProducto().trim().isEmpty()) {
            throw new BusinessException("El código del producto es obligatorio");
        }
        
        if (producto.getPrecioVenta() == null || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio de venta debe ser mayor que 0");
        }
        
        if (producto.getCategoria() == null) {
            throw new BusinessException("La categoría del producto es obligatoria");
        }
        
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            throw new BusinessException("El stock mínimo no puede ser negativo");
        }
    }

    /**
     * Calcula el tipo de IVA según la categoría del producto
     */
    private BigDecimal calcularTipoIvaProducto(Producto producto) {
        switch (producto.getCategoria()) {
            case SUPLEMENTOS_NUTRICIONALES:
            case ALIMENTACION_DEPORTIVA:
            case PRODUCTOS_DIETETICOS:
                return BigDecimal.valueOf(10.0); // IVA reducido para productos alimentarios
            
            case LIBROS_NUTRICION:
                return BigDecimal.valueOf(4.0); // IVA superreducido para libros
            
            default:
                return BigDecimal.valueOf(21.0); // IVA general
        }
    }
}
