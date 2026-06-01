package com.departamento.depa.repository;

import com.departamento.depa.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    @Query("SELECT st FROM ServiceType st WHERE " +
            "(:nombre IS NULL OR LOWER(st.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")
    List<ServiceType> findByFilters(@Param("nombre") String nombre);
}