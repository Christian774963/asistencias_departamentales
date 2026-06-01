package com.departamento.depa.service.impl;

import com.departamento.depa.entity.AdditionalService;
import com.departamento.depa.repository.AdditionalServiceRepository;
import com.departamento.depa.service.AdditionalServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdditionalServiceServiceImpl implements AdditionalServiceService {

    private final AdditionalServiceRepository repo;

    public AdditionalServiceServiceImpl(AdditionalServiceRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<AdditionalService> findByFilters(Long stayId, String roomNumero, String serviceName, Pageable pageable) {
        return repo.findByFilters(stayId, roomNumero, serviceName, pageable);
    }

    @Override
    public Optional<AdditionalService> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public AdditionalService save(AdditionalService s) {
        return repo.save(s);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public long count() {
        return repo.count();
    }
}