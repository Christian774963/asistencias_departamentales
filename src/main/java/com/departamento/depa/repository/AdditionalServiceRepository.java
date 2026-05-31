package com.departamento.depa.repository;

import com.departamento.depa.entity.AdditionalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {

    @Query("SELECT s FROM AdditionalService s " +
            "LEFT JOIN FETCH s.stay st " +
            "LEFT JOIN FETCH st.reservation r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH s.serviceType " +
            "WHERE (:stayId IS NULL OR s.stay.id = :stayId)")
    Page<AdditionalService> findByFilters(@Param("stayId") Long stayId, Pageable pageable);

    long count();
}