package com.departamento.depa.controller;

import com.departamento.depa.entity.Stay;
import com.departamento.depa.entity.Reservation;
import com.departamento.depa.repository.ReservationRepository;
import com.departamento.depa.service.StayService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/stays")
public class StayController {

    private final StayService stayService;
    private final ReservationRepository reservationRepository;

    public StayController(StayService stayService, ReservationRepository reservationRepository) {
        this.stayService = stayService;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String estado,
                       Model model) {

        Page<Stay> stays = stayService.findByFilters(estado, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("stays", stays);
        model.addAttribute("estado", estado);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", stays.getTotalPages());
        model.addAttribute("activePage", "stays"); // ✅ Sidebar Active
        model.addAttribute("stay", new Stay());
        model.addAttribute("action", "create");

        return "admin/stays";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Stay stay,
                       @RequestParam("reservationId") Long reservationId,
                       RedirectAttributes redirect) {
        try {
            // Buscar la reserva para vincularla
            Reservation res = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
            stay.setReservation(res);

            // Si es nuevo, poner estado por defecto
            if (stay.getEstado() == null) stay.setEstado("En Curso");

            stayService.save(stay);
            redirect.addFlashAttribute("msg", "Estadía registrada correctamente ✅");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/stays";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        stayService.deleteById(id);
        redirect.addFlashAttribute("msg", "Estadía eliminada 🗑️");
        return "redirect:/admin/stays";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("stay", stayService.findById(id).orElseThrow());
        model.addAttribute("action", "edit");
        return "admin/stays";
    }
}