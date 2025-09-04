package com.thunderfat.springboot.backend.model.dto.mapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

@Mapper(componentModel = "spring", uses = {DiaDietaMapper.class})
public interface PlanDietaMapper {
    PlanDietaMapper INSTANCE = Mappers.getMapper(PlanDietaMapper.class);

    // Entity to DTO mapping with calculated fields
    @Mapping(source = "id_paciente", target = "idPaciente")
    @Mapping(source = "id_nutricionista", target = "idNutricionista")
    @Mapping(source = "visible", target = "visible", qualifiedByName = "shortToBoolean")
    @Mapping(source = "intercambiable", target = "intercambiable", qualifiedByName = "shortToBoolean")
    @Mapping(source = "dias", target = "diasDieta")
    @Mapping(source = "filtrosaplicado.id", target = "filtroAplicadoId")
    @Mapping(target = "filtroAplicado", ignore = true) // Handle manually in service layer
    @Mapping(source = "fechafin", target = "isActive", qualifiedByName = "calculateIsActive")
    @Mapping(source = "fechafin", target = "daysRemaining", qualifiedByName = "calculateDaysRemaining")
    @Mapping(source = ".", target = "totalDays", qualifiedByName = "calculateTotalDays")
    @Mapping(source = ".", target = "completionPercentage", qualifiedByName = "calculateCompletionPercentage")
    PlanDietaDTO toDTO(PlanDieta planDieta);

    // DTO to Entity mapping
    @Mapping(source = "idPaciente", target = "id_paciente")
    @Mapping(source = "idNutricionista", target = "id_nutricionista")
    @Mapping(source = "visible", target = "visible", qualifiedByName = "booleanToShort")
    @Mapping(source = "intercambiable", target = "intercambiable", qualifiedByName = "booleanToShort")
    @Mapping(source = "diasDieta", target = "dias")
    @Mapping(target = "filtrosaplicado", ignore = true) // Handle separately in service layer
    PlanDieta toEntity(PlanDietaDTO dto);

    // List mappings
    List<PlanDietaDTO> toDTOList(List<PlanDieta> entities);
    List<PlanDieta> toEntityList(List<PlanDietaDTO> dtos);

    // Custom mapping methods
    @Named("shortToBoolean")
    default Boolean shortToBoolean(Short value) {
        return value != null && value == 1;
    }

    @Named("booleanToShort")
    default Short booleanToShort(Boolean value) {
        return value != null && value ? (short) 1 : (short) 0;
    }

    @Named("calculateIsActive")
    default Boolean calculateIsActive(LocalDate fechafin) {
        if (fechafin == null) return false;
        return LocalDate.now().isBefore(fechafin) || LocalDate.now().isEqual(fechafin);
    }

    @Named("calculateDaysRemaining")
    default Integer calculateDaysRemaining(LocalDate fechafin) {
        if (fechafin == null) return 0;
        LocalDate now = LocalDate.now();
        if (now.isAfter(fechafin)) return 0;
        return (int) ChronoUnit.DAYS.between(now, fechafin) + 1;
    }

    @Named("calculateTotalDays")
    default Integer calculateTotalDays(PlanDieta planDieta) {
        if (planDieta.getFechaini() == null || planDieta.getFechafin() == null) return 0;
        return (int) ChronoUnit.DAYS.between(planDieta.getFechaini(), planDieta.getFechafin()) + 1;
    }

    @Named("calculateCompletionPercentage")
    default Double calculateCompletionPercentage(PlanDieta planDieta) {
        if (planDieta.getFechaini() == null || planDieta.getFechafin() == null) return 0.0;
        
        LocalDate now = LocalDate.now();
        LocalDate startDate = planDieta.getFechaini();
        LocalDate endDate = planDieta.getFechafin();
        
        if (now.isBefore(startDate)) return 0.0;
        if (now.isAfter(endDate)) return 100.0;
        
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysCompleted = ChronoUnit.DAYS.between(startDate, now) + 1;
        
        return (daysCompleted * 100.0) / totalDays;
    }
}
