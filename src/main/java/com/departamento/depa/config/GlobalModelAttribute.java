package com.departamento.depa.config;

import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.repository.UserRepository;
import com.departamento.depa.repository.ReservationRepository; // ✅ Agregar
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository; // ✅ Agregar

    public GlobalModelAttribute(RoomRepository roomRepository,
                                UserRepository userRepository,
                                ReservationRepository reservationRepository) { // ✅ Agregar
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("totalRooms", roomRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalReservations", reservationRepository.count()); // ✅ Agregar
    }
}