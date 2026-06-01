package com.departamento.depa.service.impl;

import com.departamento.depa.entity.Room;
import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository repo;

    public RoomServiceImpl(RoomRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<Room> findByFilters(String numero, String tipo, String estado, Pageable pageable) {
        numero = (numero == null || numero.isBlank() || numero.equals("Todos")) ? null : numero;
        tipo = (tipo == null || tipo.isBlank() || tipo.equals("Todos")) ? null : tipo;
        estado = (estado == null || estado.isBlank() || estado.equals("Todos")) ? null : estado;

        return repo.findByFilters(numero, tipo, estado, pageable);
    }

    @Override public Optional<Room> findById(Long id) { return repo.findById(id); }
    @Override public Room save(Room r) { return repo.save(r); }
    @Override public void deleteById(Long id) { repo.deleteById(id); }
    public boolean hasReservations(Long roomId) {
        return repo.hasReservations(roomId) > 0;
    }
}