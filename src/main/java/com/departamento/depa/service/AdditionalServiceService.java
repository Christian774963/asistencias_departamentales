package com.departamento.depa.service;

import com.departamento.depa.entity.AdditionalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AdditionalServiceService {
    Page<AdditionalService> findByFilters(Long stayId, String roomNumero, String serviceName, Pageable pageable);
    Optional<AdditionalService> findById(Long id);
    AdditionalService save(AdditionalService service);
    void deleteById(Long id);
    long count();
}