package com.departamento.depa.service;

import com.departamento.depa.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ReservationService {
    Page<Reservation> findByFilters(Long id, String estado, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, Pageable pageable);
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
    void deleteById(Long id);
    long count();

    java.util.List<com.departamento.depa.dto.ReporteFacturacionDTO> obtenerReporteFacturacion();
}