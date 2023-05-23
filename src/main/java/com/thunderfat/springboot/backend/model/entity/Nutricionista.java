package com.thunderfat.springboot.backend.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nutricionista")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@NoArgsConstructor
public class Nutricionista extends Usuario {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;
    private String telefono;
    private String localidad;
    private String provincia;
    private String dni;
    private String direccion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutricionista", orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "nutricionista"})
    private List<Paciente> pacientes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutricionista")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Cita> citas = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutricionista")
    private List<PlanDieta> planesDietas = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutricionista")
    private List<FiltroAlimentario> filtros = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutricionista")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<PlatoPredeterminado> platos = new ArrayList<>();

    public Nutricionista(String nombre, String apellidos, String telefono, String localidad, String provincia,
                         String dni, String direccion) {
        super();
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.localidad = localidad;
        this.provincia = provincia;
        this.dni = dni;
        this.direccion = direccion;
    }

    public void addPaciente(Paciente paciente) {
        pacientes.add(paciente);
        paciente.setNutricionista(this);
    }

    public void removePaciente(Paciente paciente) {
        pacientes.remove(paciente);
        paciente.setNutricionista(null);
    }
}
