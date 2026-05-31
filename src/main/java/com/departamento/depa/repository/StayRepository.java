package com.departamento.depa.repository;

import com.departamento.depa.entity.Stay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StayRepository extends JpaRepository<Stay, Long> {

    @Query("SELECT s FROM Stay s " +
            "LEFT JOIN FETCH s.reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.room " +
            "WHERE (:estado IS NULL OR s.estado LIKE %:estado%)")
    Page<Stay> findByFilters(@Param("estado") String estado, Pageable pageable);

    long count();
}