package com.departamento.depa.service.impl;

import com.departamento.depa.entity.Reservation;
import com.departamento.depa.repository.ReservationRepository;
import com.departamento.depa.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repo;

    public ReservationServiceImpl(ReservationRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Reservation> findByFilters(String estado, String fechaInicio, String fechaFin, Pageable pageable) {
        estado = (estado == null || estado.isBlank() || "Todos".equals(estado)) ? null : estado;
        fechaInicio = (fechaInicio == null || fechaInicio.isBlank()) ? null : fechaInicio;
        fechaFin = (fechaFin == null || fechaFin.isBlank()) ? null : fechaFin;
        return repo.findByFilters(estado, fechaInicio, fechaFin, pageable);
    }

    @Override public Optional<Reservation> findById(Long id) { return repo.findById(id); }
    @Override public Reservation save(Reservation r) { return repo.save(r); }
    @Override public void deleteById(Long id) { repo.deleteById(id); }
    @Override public long count() { return repo.count(); }
}