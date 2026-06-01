package com.departamento.depa.controller;

import com.departamento.depa.entity.Reservation;
import com.departamento.depa.service.ReservationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String id,
                       @RequestParam(required = false) String estado,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                       Model model) {

        // ✅ CONVERSIÓN MANUAL: String vacío o null -> Long null
        Long idLong = null;
        if (id != null && !id.trim().isEmpty()) {
            try { idLong = Long.parseLong(id.trim()); } catch (NumberFormatException e) { idLong = null; }
        }

        Page<Reservation> reservations = reservationService.findByFilters(idLong, estado, fechaInicio, fechaFin,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("reservations", reservations);
        model.addAttribute("id", id);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservations.getTotalPages());
        model.addAttribute("activePage", "reservations");
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("action", "create");

        return "admin/reservations";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Reservation reservation,
                       @RequestParam(name = "userId", required = false) Long userId,
                       @RequestParam(name = "roomId", required = false) Long roomId,
                       RedirectAttributes redirect) {
        try {
            // ✅ Setear manualmente las relaciones usando solo los IDs
            if (userId != null && userId > 0) {
                com.departamento.depa.entity.User user = new com.departamento.depa.entity.User();
                user.setId(userId);
                reservation.setUser(user);
            }
            if (roomId != null && roomId > 0) {
                com.departamento.depa.entity.Room room = new com.departamento.depa.entity.Room();
                room.setId(roomId);
                reservation.setRoom(room);
            }

            reservationService.save(reservation);
            redirect.addFlashAttribute("msg", reservation.getId() == null ? "Reserva creada correctamente" : "Reserva actualizada correctamente");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "❌ No se puede guardar: El registro viola restricciones de la base de datos. Verifique los IDs de cliente y habitación.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            reservationService.deleteById(id);
            redirect.addFlashAttribute("msg", "Reserva eliminada correctamente");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "❌ No se puede eliminar: La reserva tiene estadías o servicios asociados. Elimine primero los registros relacionados.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/reservations";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id).orElseThrow();
        model.addAttribute("reservation", reservation);
        model.addAttribute("action", "edit");
        return "admin/reservations";
    }
}