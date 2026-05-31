package com.departamento.depa.controller;

import com.departamento.depa.entity.Reservation;
import com.departamento.depa.entity.Room;
import com.departamento.depa.entity.User;
import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.repository.UserRepository;
import com.departamento.depa.service.ReservationService;
import com.departamento.depa.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ReservationController(ReservationService reservationService,  RoomRepository roomRepository, UserRepository userRepository) {
        this.reservationService = reservationService;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String estado,
                       @RequestParam(required = false) String fechaInicio,
                       @RequestParam(required = false) String fechaFin,
                       Model model) {

        estado = (estado == null || estado.isBlank() || "Todos".equals(estado)) ? null : estado;
        fechaInicio = (fechaInicio == null || fechaInicio.isBlank()) ? null : fechaInicio;
        fechaFin = (fechaFin == null || fechaFin.isBlank()) ? null : fechaFin;

        Page<Reservation> reservations = reservationService.findByFilters(
                estado, fechaInicio, fechaFin,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        model.addAttribute("reservations", reservations);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservations.getTotalPages());
        model.addAttribute("activePage", "reservations"); // ✅ Para sidebar
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("action", "create");

        return "admin/reservations";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Reservation reservation,
                       @RequestParam("userId") Long userId,
                       @RequestParam("roomId") Long roomId,
                       RedirectAttributes redirect) {
        try {
            // Buscar y asignar el usuario
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            reservation.setUser(user);

            // Buscar y asignar la habitación
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
            reservation.setRoom(room);

            // Establecer fecha de creación si es nueva
            if (reservation.getId() == null) {
                reservation.setFechaCreacion(java.time.LocalDate.now());
            }

            reservationService.save(reservation);
            redirect.addFlashAttribute("msg", "Reserva guardada correctamente ✅");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        reservationService.deleteById(id);
        redirect.addFlashAttribute("msg", "Reserva eliminada 🗑️");
        return "redirect:/admin/reservations";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", reservationService.findById(id).orElseThrow());
        model.addAttribute("action", "edit");
        model.addAttribute("estado", "");
        model.addAttribute("fechaInicio", "");
        model.addAttribute("fechaFin", "");
        return "admin/reservations";
    }
}