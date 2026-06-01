package com.departamento.depa.service;

import com.departamento.depa.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface RoomService {
    Page<Room> findByFilters(String numero, String tipo, String estado, Pageable pageable);
    Optional<Room> findById(Long id);
    Room save(Room room);
    void deleteById(Long id);
    boolean hasReservations(Long roomId);
}