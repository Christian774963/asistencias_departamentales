package com.departamento.depa.service;

import com.departamento.depa.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ReservationService {
    Page<Reservation> findByFilters(String estado, String fechaInicio, String fechaFin, Pageable pageable);
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
    void deleteById(Long id);
    long count();
}