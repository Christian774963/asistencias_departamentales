package com.departamento.depa.controller;

import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AdminController(RoomRepository roomRepository,
                           UserRepository userRepository
                           ) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        // Estadísticas reales desde la BD
        long totalRooms = roomRepository.count();
        long totalUsers = userRepository.count();

        // Calcular ocupación (habitaciones ocupadas / total)
        long occupiedRooms = roomRepository.findByEstado("Ocupada").size();
        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0;

        model.addAttribute("userEmail", auth.getName());
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("occupancyRate", String.format("%.0f%%", occupancyRate));
        model.addAttribute("activePage", "dashboard");
        return "admin/menu-admin";
    }
}