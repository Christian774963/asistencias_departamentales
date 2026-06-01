package com.departamento.depa.service.impl;

import com.departamento.depa.entity.Reservation;
import com.departamento.depa.repository.ReservationRepository;
import com.departamento.depa.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.departamento.depa.dto.ReporteFacturacionDTO;
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repo;

    public ReservationServiceImpl(ReservationRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Reservation> findByFilters(Long id, String estado, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, Pageable pageable) {
        return repo.findByFilters(id, estado, fechaInicio, fechaFin, pageable);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return repo.save(reservation);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public java.util.List<com.departamento.depa.dto.ReporteFacturacionDTO> obtenerReporteFacturacion() {
        // Usamos "repo", que es el nombre real de tu variable en la línea 14
        java.util.List<Object[]> filas = repo.obtenerDatosVistaFacturacion();
        java.util.List<com.departamento.depa.dto.ReporteFacturacionDTO> lista = new java.util.ArrayList<>();

        for (Object[] fila : filas) {
            lista.add(new com.departamento.depa.dto.ReporteFacturacionDTO(
                    fila[0] != null ? ((Number) fila[0]).longValue() : null,
                    fila[1] != null ? ((Number) fila[1]).longValue() : null,
                    fila[2] != null ? ((Number) fila[2]).longValue() : null,
                    fila[3] != null ? ((Number) fila[3]).longValue() : null,
                    fila[4] != null ? ((Number) fila[4]).longValue() : null
            ));
        }
        return lista;
    }
}