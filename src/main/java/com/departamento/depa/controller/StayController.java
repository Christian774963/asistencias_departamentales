package com.departamento.depa.controller;

import com.departamento.depa.entity.Stay;
import com.departamento.depa.entity.Reservation;
import com.departamento.depa.repository.ReservationRepository;
import com.departamento.depa.repository.StayRepository;
import com.departamento.depa.service.StayService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/stays")
public class StayController {

    private final StayService stayService;
    private final ReservationRepository reservationRepository;
    private final StayRepository stayRepository;

    public StayController(StayService stayService,
                          ReservationRepository reservationRepository,
                          StayRepository stayRepository) {
        this.stayService = stayService;
        this.reservationRepository = reservationRepository;
        this.stayRepository = stayRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String id,
                       @RequestParam(required = false) String estado,
                       Model model) {

        Long idLong = null;
        if (id != null && !id.trim().isEmpty()) {
            try { idLong = Long.parseLong(id.trim()); } catch (NumberFormatException e) { idLong = null; }
        }
        String estadoParam = (estado != null && !estado.trim().isEmpty()) ? estado : null;

        Page<Stay> stays = stayService.findByFilters(idLong, estadoParam,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("stays", stays);
        model.addAttribute("id", id);
        model.addAttribute("estado", estado);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", stays.getTotalPages());
        model.addAttribute("activePage", "stays");
        model.addAttribute("stay", new Stay());

        return "admin/stays";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Stay stay,
                       @RequestParam(name = "reservationId", required = false) Long reservationId,
                       RedirectAttributes redirect) {
        try {
            log.info("🔄 Guardando estadía - reservationId: {}, fechaEntrada: {}", reservationId, stay.getFechaEntradaReal());

            if (reservationId == null || reservationId <= 0) {
                log.error("❌ reservationId inválido: {}", reservationId);
                throw new RuntimeException("Debe ingresar un ID de reserva válido");
            }

            // ✅ ELIMINAR: Ya no validamos si existe otra estadía (ahora permitimos múltiples)
            // if (stayRepository.existsByReservationId(reservationId)) { ... }

            Reservation res = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> {
                        log.error("❌ Reserva no encontrada: {}", reservationId);
                        return new RuntimeException("Reserva no encontrada con ID: " + reservationId);
                    });

            stay.setReservation(res);

            if (stay.getFechaEntradaReal() == null) {
                log.error("❌ fechaEntradaReal es null");
                throw new RuntimeException("La fecha de Check-In es obligatoria");
            }

            if (stay.getEstado() == null) {
                stay.setEstado("En Curso");
            }

            Stay saved = stayService.save(stay);
            log.info("✅ Estadía guardada exitosamente - ID: {}", saved.getId());
            redirect.addFlashAttribute("msg", "Estadía registrada correctamente ✅");

        } catch (DataIntegrityViolationException e) {
            log.error("❌ DataIntegrityViolationException: {}", e.getMessage());
            redirect.addFlashAttribute("error", "❌ Error de base de datos: " + e.getRootCause().getMessage());
        } catch (Exception e) {
            log.error("❌ Error al guardar estadía: {}", e.getMessage(), e);
            redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            redirect.addFlashAttribute("stay", stay);
        }
        return "redirect:/admin/stays";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            stayService.deleteById(id);
            redirect.addFlashAttribute("msg", "Estadía eliminada correctamente 🗑️");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "❌ No se puede eliminar: La estadía tiene servicios adicionales asociados.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/stays";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("stay", stayService.findById(id).orElseThrow());
        model.addAttribute("action", "edit");
        return "admin/stays";
    }
}