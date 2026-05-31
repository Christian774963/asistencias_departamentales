package com.departamento.depa.repository;

import com.departamento.depa.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +  // ✅ CRÍTICO
            "LEFT JOIN FETCH r.room " +  // ✅ CRÍTICO
            "WHERE (:estado IS NULL OR r.estado LIKE %:estado%) AND " +
            "(:fechaInicio IS NULL OR r.fechaInicio >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR r.fechaFin <= :fechaFin)")
    Page<Reservation> findByFilters(
            @Param("estado") String estado,
            @Param("fechaInicio") String fechaInicio,
            @Param("fechaFin") String fechaFin,
            Pageable pageable
    );

    List<Reservation> findByEstado(String estado);
}