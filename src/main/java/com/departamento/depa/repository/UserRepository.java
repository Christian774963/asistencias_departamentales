package com.departamento.depa.repository;

import com.departamento.depa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Búsqueda por Email (para el Login, ya existente)
    Optional<User> findByEmail(String email);

    // ✅ FILTROS DINÁMICOS AND
    @Query("SELECT u FROM User u WHERE " +
            "(:nombre IS NULL OR u.nombre LIKE %:nombre%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:rol IS NULL OR u.rol LIKE %:rol%)")
    Page<User> findByFilters(
            @Param("nombre") String nombre,
            @Param("email") String email,
            @Param("rol") String rol,
            Pageable pageable
    );

    // Conteo por Rol (opcional para estadísticas futuras)
    long countByRol(String rol);
}