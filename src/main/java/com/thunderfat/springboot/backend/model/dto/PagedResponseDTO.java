package com.thunderfat.springboot.backend.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Standardized pagination response DTO following API contract guidelines
 * Provides uniform pagination structure across all list endpoints
 * 
 * @param <T> Type of items in the paginated response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Paginated response wrapper")
public class PagedResponseDTO<T> {
    
    @Schema(description = "List of items for current page")
    private List<T> items;
    
    @Schema(description = "Current page number (0-based)", example = "0")
    private int page;
    
    @Schema(description = "Number of items per page", example = "20")
    private int size;
    
    @Schema(description = "Total number of items across all pages", example = "150")
    private long total;
    
    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;
    
    @Schema(description = "Whether this is the first page")
    private boolean first;
    
    @Schema(description = "Whether this is the last page")
    private boolean last;
    
    @Schema(description = "Whether there is a next page")
    private boolean hasNext;
    
    @Schema(description = "Whether there is a previous page")
    private boolean hasPrevious;
    
    /**
     * Factory method to create paginated response from Spring Data Page
     */
    public static <T> PagedResponseDTO<T> of(org.springframework.data.domain.Page<T> page) {
        return PagedResponseDTO.<T>builder()
            .items(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .total(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }
    
    /**
     * Factory method for non-paginated lists (converts to single page)
     */
    public static <T> PagedResponseDTO<T> ofList(List<T> items) {
        return PagedResponseDTO.<T>builder()
            .items(items)
            .page(0)
            .size(items.size())
            .total(items.size())
            .totalPages(1)
            .first(true)
            .last(true)
            .hasNext(false)
            .hasPrevious(false)
            .build();
    }
    
    /**
     * Legacy compatibility method - maps old field names to new contract
     * @deprecated Use the new factory methods instead
     */
    @Deprecated
    public static <T> PagedResponseDTO<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PagedResponseDTO.<T>builder()
                .items(content)  // Map content -> items
                .page(page)
                .size(size)
                .total(totalElements)  // Map totalElements -> total
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .build();
    }
}
