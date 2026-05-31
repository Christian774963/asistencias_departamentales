package com.departamento.depa.repository;

import com.departamento.depa.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE " +
            "(:numero IS NULL OR r.numero LIKE %:numero%) AND " +
            "(:tipo IS NULL OR r.tipo LIKE %:tipo%) AND " +
            "(:estado IS NULL OR r.estado LIKE %:estado%)")
    Page<Room> findByFilters(
            @Param("numero") String numero,
            @Param("tipo") String tipo,
            @Param("estado") String estado,
            Pageable pageable
    );
    List<Room> findByEstado(String estado);

}