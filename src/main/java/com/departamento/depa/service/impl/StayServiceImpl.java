package com.departamento.depa.service.impl;

import com.departamento.depa.entity.Stay;
import com.departamento.depa.repository.StayRepository;
import com.departamento.depa.service.StayService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StayServiceImpl implements StayService {

    private final StayRepository repo;

    public StayServiceImpl(StayRepository repo) { this.repo = repo; }

    @Override public Page<Stay> findByFilters(String estado, Pageable pageable) {
        return repo.findByFilters((estado != null && !estado.isBlank() && !"Todos".equals(estado)) ? estado : null, pageable);
    }
    @Override public Optional<Stay> findById(Long id) { return repo.findById(id); }
    @Override public Stay save(Stay s) { return repo.save(s); }
    @Override public void deleteById(Long id) { repo.deleteById(id); }
    @Override public long count() { return repo.count(); }
}