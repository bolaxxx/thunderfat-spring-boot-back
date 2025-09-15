package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.ValidationGroups;
import com.thunderfat.springboot.backend.model.service.IAlimentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Modern Alimento REST Controller - Spring Boot 2025 Best Practices
 * 
 * Features:
 * - DTO pattern with comprehensive validation
 * - Pagination and sorting support
 * - Role-based security with @PreAuthorize
 * - Standardized ManualApiResponseDTO responses
 * - Comprehensive OpenAPI documentation
 * - Business logic delegated to service layer
 * - Global exception handling integration
 * - Structured logging with SLF4J
 * 
 * @author ThunderFat Development Team
 * @version 2025.1
 */
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/alimentos")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Alimentos", description = "API para gestión de alimentos y información nutricional")
@SecurityRequirement(name = "bearerAuth")
public class AlimentoRestController {
    
    private final IAlimentoService alimentoService;
    
    // ====================== READ OPERATIONS ======================
    
    @Operation(
        summary = "Listar todos los alimentos con paginación",
        description = "Obtiene una lista paginada de todos los alimentos disponibles en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<Page<AlimentoDTO>>> listarTodos(
            @PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) 
            @Parameter(description = "Parámetros de paginación (page, size, sort)") 
            Pageable pageable) {
        
        log.info("GET /alimentos/todos - Página: {}, Tamaño: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<AlimentoDTO> alimentos = alimentoService.listarAlimentos(pageable);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos, 
            String.format("Se encontraron %d alimentos (página %d de %d)", 
                         alimentos.getNumberOfElements(),
                         alimentos.getNumber() + 1, 
                         alimentos.getTotalPages())
        ));
    }
    
    @Operation(
        summary = "Buscar alimento por ID",
        description = "Obtiene la información completa de un alimento específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alimento encontrado"),
        @ApiResponse(responseCode = "404", description = "Alimento no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<AlimentoDTO>> buscarPorId(
            @Parameter(description = "ID del alimento", example = "1")
            @PathVariable @Positive(message = "El ID debe ser un número positivo") Integer id) {
        
        log.info("GET /alimentos/{} - Buscando alimento por ID", id);
        
        AlimentoDTO alimento = alimentoService.buscarPorId(id);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimento, 
            "Alimento encontrado: " + alimento.getNombre()
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos por nombre",
        description = "Busca alimentos que contengan el término especificado en su nombre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda completada"),
        @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ManualApiResponseDTO<Page<AlimentoDTO>>> buscarPorNombre(
            @Parameter(description = "Nombre del alimento a buscar", example = "pollo")
            @RequestParam String nombre,
            @PageableDefault(size = 20, sort = "nombre") 
            Pageable pageable) {
        
        log.info("GET /alimentos/buscar?nombre={} - Página: {}", nombre, pageable.getPageNumber());
        
        Page<AlimentoDTO> alimentos = alimentoService.buscarPorNombre(nombre, pageable);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos que contienen '%s'", 
                         alimentos.getTotalElements(), nombre)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos por estado",
        description = "Filtra alimentos por su estado (fresco, procesado, congelado, etc.)"
    )
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ManualApiResponseDTO<Page<AlimentoDTO>>> buscarPorEstado(
            @Parameter(description = "Estado del alimento", example = "fresco")
            @PathVariable String estado,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /alimentos/estado/{} - Página: {}", estado, pageable.getPageNumber());
        
        Page<AlimentoDTO> alimentos = alimentoService.buscarPorEstado(estado, pageable);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos con estado '%s'", 
                         alimentos.getTotalElements(), estado)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos por rango calórico",
        description = "Encuentra alimentos dentro de un rango específico de calorías"
    )
    @GetMapping("/calorias")
    public ResponseEntity<ManualApiResponseDTO<Page<AlimentoDTO>>> buscarPorRangoCalorias(
            @Parameter(description = "Calorías mínimas", example = "100")
            @RequestParam @Min(value = 0, message = "Las calorías mínimas no pueden ser negativas") Double minCal,
            @Parameter(description = "Calorías máximas", example = "500")
            @RequestParam @Positive(message = "Las calorías máximas deben ser positivas") Double maxCal,
            @PageableDefault(size = 20, sort = "cal") Pageable pageable) {
        
        log.info("GET /alimentos/calorias?minCal={}&maxCal={}", minCal, maxCal);
        
        Page<AlimentoDTO> alimentos = alimentoService.buscarPorRangoCalorias(minCal, maxCal, pageable);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos entre %.1f y %.1f calorías", 
                         alimentos.getTotalElements(), minCal, maxCal)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos altos en proteína",
        description = "Obtiene alimentos con contenido proteico superior al umbral especificado"
    )
    @GetMapping("/alto-proteina")
    public ResponseEntity<ManualApiResponseDTO<List<AlimentoDTO>>> buscarAlimentosAltoProteina(
            @Parameter(description = "Umbral mínimo de proteínas (gramos)", example = "15.0")
            @RequestParam @Positive(message = "El umbral de proteínas debe ser positivo") Double threshold) {
        
        log.info("GET /alimentos/alto-proteina?threshold={}", threshold);
        
        List<AlimentoDTO> alimentos = alimentoService.buscarAlimentosAltoProteina(threshold);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos con más de %.1fg de proteína", 
                         alimentos.size(), threshold)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos bajos en calorías",
        description = "Obtiene alimentos con contenido calórico inferior al umbral especificado"
    )
    @GetMapping("/baja-caloria")
    public ResponseEntity<ManualApiResponseDTO<List<AlimentoDTO>>> buscarAlimentosBajaCaloria(
            @Parameter(description = "Umbral máximo de calorías", example = "100.0")
            @RequestParam @Positive(message = "El umbral de calorías debe ser positivo") Double threshold) {
        
        log.info("GET /alimentos/baja-caloria?threshold={}", threshold);
        
        List<AlimentoDTO> alimentos = alimentoService.buscarAlimentosBajaCaloria(threshold);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos con menos de %.1f calorías", 
                         alimentos.size(), threshold)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos ricos en vitamina específica",
        description = "Encuentra alimentos con alto contenido de una vitamina específica"
    )
    @GetMapping("/vitamina/{vitamin}")
    public ResponseEntity<ManualApiResponseDTO<List<AlimentoDTO>>> buscarAlimentosRicosEnVitamina(
            @Parameter(description = "Nombre de la vitamina", example = "vitaminaC")
            @PathVariable String vitamin,
            @Parameter(description = "Umbral mínimo de contenido vitamínico", example = "10.0")
            @RequestParam @Positive(message = "El umbral vitamínico debe ser positivo") Double threshold) {
        
        log.info("GET /alimentos/vitamina/{}?threshold={}", vitamin, threshold);
        
        List<AlimentoDTO> alimentos = alimentoService.buscarAlimentosRicosEnVitamina(vitamin, threshold);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos ricos en %s", 
                         alimentos.size(), vitamin)
        ));
    }
    
    @Operation(
        summary = "Buscar alimentos para dieta específica",
        description = "Encuentra alimentos que cumplan criterios nutricionales específicos para planes dietéticos"
    )
    @GetMapping("/para-dieta")
    public ResponseEntity<ManualApiResponseDTO<Page<AlimentoDTO>>> buscarAlimentosParaDieta(
            @Parameter(description = "Calorías mínimas") @RequestParam Double minCal,
            @Parameter(description = "Calorías máximas") @RequestParam Double maxCal,
            @Parameter(description = "Proteína mínima") @RequestParam Double minProtein,
            @Parameter(description = "Grasa máxima") @RequestParam Double maxFat,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("GET /alimentos/para-dieta - Criterios: cal({}-{}), prot(min:{}), grasa(max:{})", 
                minCal, maxCal, minProtein, maxFat);
        
        Page<AlimentoDTO> alimentos = alimentoService.buscarAlimentosParaDieta(
            minCal, maxCal, minProtein, maxFat, pageable);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Se encontraron %d alimentos adecuados para la dieta", 
                         alimentos.getTotalElements())
        ));
    }
    
    @Operation(
        summary = "Listar alimentos para componentes select",
        description = "Obtiene una lista simple de alimentos para usar en dropdowns y componentes de selección"
    )
    @GetMapping("/select")
    public ResponseEntity<ManualApiResponseDTO<List<AlimentoDTO>>> listarParaSelect() {
        log.info("GET /alimentos/select - Lista para componentes select");
        
        List<AlimentoDTO> alimentos = alimentoService.listarParaSelect();
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentos,
            String.format("Lista de %d alimentos para selección", alimentos.size())
        ));
    }
    
    // ====================== WRITE OPERATIONS (SECURED) ======================
    
    @Operation(
        summary = "Crear nuevo alimento",
        description = "Crea un nuevo alimento en el sistema. Requiere rol NUTRICIONISTA o ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Alimento creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
        @ApiResponse(responseCode = "409", description = "Conflicto - nombre duplicado")
    })
    @PostMapping("/save")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ResponseEntity<ManualApiResponseDTO<AlimentoDTO>> crear(
            @Parameter(description = "Datos del nuevo alimento", 
                      content = @Content(schema = @Schema(implementation = AlimentoDTO.class)))
            @RequestBody @Valid AlimentoDTO alimentoDTO) {
        
        log.info("POST /alimentos/save - Creando alimento: {}", alimentoDTO.getNombre());
        
        AlimentoDTO alimentoCreado = alimentoService.crear(alimentoDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ManualApiResponseDTO.success(
                alimentoCreado,
                "Alimento creado exitosamente: " + alimentoCreado.getNombre()
            ));
    }
    
    @Operation(
        summary = "Actualizar alimento completo",
        description = "Actualiza todos los campos de un alimento existente. Requiere rol NUTRICIONISTA o ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alimento actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
        @ApiResponse(responseCode = "404", description = "Alimento no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ResponseEntity<ManualApiResponseDTO<AlimentoDTO>> actualizar(
            @Parameter(description = "ID del alimento", example = "1")
            @PathVariable @Positive(message = "El ID debe ser un número positivo") Integer id,
            @Parameter(description = "Datos actualizados del alimento")
            @RequestBody @Validated(ValidationGroups.Update.class) AlimentoDTO alimentoDTO) {
        
        log.info("PUT /alimentos/{} - Actualizando alimento", id);
        
        AlimentoDTO alimentoActualizado = alimentoService.actualizar(id, alimentoDTO);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentoActualizado,
            "Alimento actualizado exitosamente: " + alimentoActualizado.getNombre()
        ));
    }
    
    @Operation(
        summary = "Actualización parcial de alimento",
        description = "Actualiza solo los campos especificados de un alimento. Requiere rol NUTRICIONISTA o ADMIN."
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ResponseEntity<ManualApiResponseDTO<AlimentoDTO>> actualizarParcial(
            @Parameter(description = "ID del alimento", example = "1")
            @PathVariable @Positive(message = "El ID debe ser un número positivo") Integer id,
            @Parameter(description = "Campos a actualizar del alimento")
            @RequestBody AlimentoDTO alimentoDTO) {
        
        log.info("PATCH /alimentos/{} - Actualización parcial", id);
        
        AlimentoDTO alimentoActualizado = alimentoService.actualizarParcial(id, alimentoDTO);
        
        return ResponseEntity.ok(ManualApiResponseDTO.success(
            alimentoActualizado,
            "Alimento actualizado parcialmente: " + alimentoActualizado.getNombre()
        ));
    }
    
    @Operation(
        summary = "Eliminar alimento",
        description = "Elimina un alimento del sistema. Requiere rol NUTRICIONISTA o ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Alimento eliminado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
        @ApiResponse(responseCode = "404", description = "Alimento no encontrado")
    })
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "ID del alimento a eliminar", example = "1")
            @PathVariable @Positive(message = "El ID debe ser un número positivo") Integer id) {
        
        log.info("DELETE /alimentos/eliminar/{} - Eliminando alimento", id);
        
        alimentoService.eliminar(id);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ManualApiResponseDTO.success(null, "Alimento eliminado exitosamente"));
    }
}
