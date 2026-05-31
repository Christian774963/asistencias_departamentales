package com.departamento.depa.config;

import com.departamento.depa.repository.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final StayRepository stayRepository;
    private final AdditionalServiceRepository additionalServiceRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public GlobalModelAttribute(RoomRepository roomRepository,
                                UserRepository userRepository,
                                ReservationRepository reservationRepository,
                                StayRepository stayRepository,
                                AdditionalServiceRepository additionalServiceRepository,
                                ServiceTypeRepository serviceTypeRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.stayRepository = stayRepository;
        this.additionalServiceRepository = additionalServiceRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("totalRooms", roomRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalReservations", reservationRepository.count());
        model.addAttribute("totalStays", stayRepository.count());
       // model.addAttribute("totalServices", additionalServiceRepository.count());
        // Opcional: model.addAttribute("totalServiceTypes", serviceTypeRepository.count());
    }
}