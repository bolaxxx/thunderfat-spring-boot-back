package com.thunderfat.springboot.backend.model.dto;

import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO para manejar respuestas paginadas
 * @param <T> Tipo de datos de la lista
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    
    public static <T> PagedResponseDTO<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return PagedResponseDTO.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .empty(content.isEmpty())
                .build();
    }
}
