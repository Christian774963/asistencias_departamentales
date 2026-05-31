package com.departamento.depa.service;

import com.departamento.depa.entity.Stay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface StayService {
    Page<Stay> findByFilters(String estado, Pageable pageable);
    Optional<Stay> findById(Long id);
    Stay save(Stay stay);
    void deleteById(Long id);
    long count();
}