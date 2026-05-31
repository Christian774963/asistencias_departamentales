package com.departamento.depa.service;

import com.departamento.depa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    long count();
    Page<User> findByFilters(String nombre, String email, String rol, Pageable pageable);
    boolean hasReservations(Long userId);
}