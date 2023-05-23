package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

public interface IComidaService {
    void insertar(Comida comida);
    void eliminar(int id_comida);
    List<Comida> listaPorPlanDieta(PlanDieta planDieta);
    Comida buscarPorID(int id_comida);
    List<PlatoPredeterminado> bucarcambios(int id_paciente, int id_plato);
}
