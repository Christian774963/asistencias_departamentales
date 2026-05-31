package com.departamento.depa.controller;

import com.departamento.depa.entity.DashboardStats;
import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.repository.UserRepository;
import com.departamento.depa.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final DashboardService dashboardService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AdminController(RoomRepository roomRepository,
                           UserRepository userRepository,
                           DashboardService dashboardService
                           ) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        // Obtener estadísticas completas
        DashboardStats stats = dashboardService.getDashboardStats();

        model.addAttribute("userEmail", auth.getName());
        model.addAttribute("activePage", "dashboard");

        // Agregar estadísticas al modelo
        model.addAttribute("totalRooms", stats.getTotalRooms());
        model.addAttribute("totalUsers", stats.getTotalUsers());
        model.addAttribute("totalReservations", stats.getTotalReservations());
        model.addAttribute("occupancyRate", stats.getOccupancyRate().intValue());
        model.addAttribute("totalRevenue", stats.getTotalRevenue());
        model.addAttribute("monthlyRevenue", stats.getMonthlyRevenue());
        model.addAttribute("roomTypeStats", stats.getRoomTypeStats());

        return "admin/menu-admin";
    }
}