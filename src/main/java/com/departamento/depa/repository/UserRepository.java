package com.departamento.depa.repository;

import com.departamento.depa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Búsqueda por Email (para el Login)
    Optional<User> findByEmail(String email);

    // FILTROS DINÁMICOS - COMPATIBLE CON POSTGRESQL
    @Query("SELECT u FROM User u WHERE " +
            "(:id IS NULL OR u.id = :id) AND " +
            "(:nombre IS NULL OR u.nombre ILIKE %:nombre%) AND " +
            "(:email IS NULL OR u.email ILIKE %:email%) AND " +
            "(:rol IS NULL OR u.rol = :rol)")
    Page<User> findByFilters(@Param("id") Long id,
                             @Param("nombre") String nombre,
                             @Param("email") String email,
                             @Param("rol") String rol,
                             Pageable pageable);

    // Conteo por Rol (opcional para estadísticas)
    long countByRol(String rol);
}