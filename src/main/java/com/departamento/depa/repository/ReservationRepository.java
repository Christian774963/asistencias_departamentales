package com.departamento.depa.repository;

import com.departamento.depa.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.room " +
            "WHERE (:id IS NULL OR r.id = :id) " +
            "AND (:estado IS NULL OR LENGTH(TRIM(:estado)) = 0 OR r.estado = :estado) " +
            "AND (:fechaInicio IS NULL OR r.fechaInicio = :fechaInicio) " +
            "AND (:fechaFin IS NULL OR r.fechaFin = :fechaFin)")
    Page<Reservation> findByFilters(@Param("id") Long id,
                                    @Param("estado") String estado,
                                    @Param("fechaInicio") LocalDate fechaInicio,
                                    @Param("fechaFin") LocalDate fechaFin,
                                    Pageable pageable);

    List<Reservation> findByEstado(String estado);
    boolean existsByUserId(Long userId);
}