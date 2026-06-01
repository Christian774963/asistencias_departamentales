package com.departamento.depa.repository;

import com.departamento.depa.entity.AdditionalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServiceRepository extends JpaRepository<AdditionalService, Long> {

    @Query("SELECT s FROM AdditionalService s " +
            "LEFT JOIN FETCH s.stay st " +
            "LEFT JOIN FETCH st.reservation r " +
            "LEFT JOIN FETCH r.user u " +
            "LEFT JOIN FETCH r.room rm " +
            "LEFT JOIN FETCH s.serviceType stp " +
            "WHERE (:stayId IS NULL OR st.id = :stayId) " +  // ✅ Espacio después de AND
            "AND (:roomNumero IS NULL OR rm.numero LIKE %:roomNumero%) " +
            "AND (:serviceName IS NULL OR stp.nombre LIKE %:serviceName%)")
    Page<AdditionalService> findByFilters(@Param("stayId") Long stayId,
                                          @Param("roomNumero") String roomNumero,
                                          @Param("serviceName") String serviceName,
                                          Pageable pageable);

    long count();
}