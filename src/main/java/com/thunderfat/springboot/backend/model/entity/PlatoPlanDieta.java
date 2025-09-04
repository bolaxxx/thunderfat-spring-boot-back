package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="platoplandieta")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PlatoPlanDieta extends Plato {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private double cantidad;
    
    // Constructor
    public PlatoPlanDieta(double cantidad) {
        super(); // Call no-args constructor of Plato
        this.cantidad = cantidad;
    }
}
