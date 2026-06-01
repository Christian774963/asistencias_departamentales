package com.departamento.depa.controller;

import com.departamento.depa.entity.AdditionalService;
import com.departamento.depa.entity.Stay;
import com.departamento.depa.entity.ServiceType;
import com.departamento.depa.repository.StayRepository;
import com.departamento.depa.repository.ServiceTypeRepository;
import com.departamento.depa.service.AdditionalServiceService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/services")
public class AdditionalServiceController {

    private final AdditionalServiceService serviceService;
    private final StayRepository stayRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public AdditionalServiceController(AdditionalServiceService serviceService,
                                       StayRepository stayRepository,
                                       ServiceTypeRepository serviceTypeRepository) {
        this.serviceService = serviceService;
        this.stayRepository = stayRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String stayId,  // ✅ CAMBIO: String en lugar de Long
                       @RequestParam(required = false) String roomNumero,
                       @RequestParam(required = false) String serviceName,
                       Model model) {

        // ✅ CONVERSIÓN MANUAL: String vacío o null -> Long null
        Long stayIdLong = null;
        if (stayId != null && !stayId.trim().isEmpty()) {
            try {
                stayIdLong = Long.parseLong(stayId.trim());
            } catch (NumberFormatException e) {
                stayIdLong = null;
            }
        }

        Page<AdditionalService> services = serviceService.findByFilters(stayIdLong, roomNumero, serviceName,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("services", services);
        model.addAttribute("stayId", stayId);  // Mantener como String para el formulario
        model.addAttribute("roomNumero", roomNumero);
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", services.getTotalPages());
        model.addAttribute("activePage", "services");
        model.addAttribute("service", new AdditionalService());
        model.addAttribute("action", "create");
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());

        return "admin/services";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AdditionalService service,
                       @RequestParam Long stayId,
                       @RequestParam Long serviceTypeId,
                       RedirectAttributes redirect) {
        try {
            Stay stay = stayRepository.findById(stayId)
                    .orElseThrow(() -> new RuntimeException("Estadía no encontrada"));
            service.setStay(stay);

            ServiceType type = serviceTypeRepository.findById(serviceTypeId)
                    .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));
            service.setServiceType(type);

            if (service.getFechaRegistro() == null) {
                service.setFechaRegistro(LocalDateTime.now());
            }

            serviceService.save(service);
            redirect.addFlashAttribute("msg", "Servicio registrado correctamente ✅");

        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "❌ Error de base de datos: " + e.getRootCause().getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            serviceService.deleteById(id);
            redirect.addFlashAttribute("msg", "Servicio eliminado correctamente 🗑️");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "❌ No se puede eliminar: El servicio tiene dependencias.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceService.findById(id).orElseThrow());
        model.addAttribute("action", "edit");
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        return "admin/services";
    }
}