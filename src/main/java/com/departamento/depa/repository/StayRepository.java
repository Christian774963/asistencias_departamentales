package com.departamento.depa.repository;

import com.departamento.depa.entity.Stay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StayRepository extends JpaRepository<Stay, Long> {

    @Query("SELECT s FROM Stay s " +
            "LEFT JOIN FETCH s.reservation r " +
            "LEFT JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.room rm " +
            "WHERE (:id IS NULL OR s.id = :id) " +
            "AND (:estado IS NULL OR :estado = '' OR s.estado = :estado)")
    Page<Stay> findByFilters(@Param("id") Long id,
                             @Param("estado") String estado,
                             Pageable pageable);

    long count();
    List<Stay> findByReservationId(Long reservationId);
}