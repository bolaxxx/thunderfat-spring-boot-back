package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="platopredeterminado")
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PlatoPredeterminado extends Plato {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The nutritionist who created this predetermined dish
     * Using FetchType.LAZY for performance and CascadeType.MERGE to avoid accidental deletions
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_nutricionista")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Nutricionista nutricionista;
}
