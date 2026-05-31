package com.departamento.depa.controller;

import com.departamento.depa.entity.AdditionalService;
import com.departamento.depa.entity.Stay;
import com.departamento.depa.entity.ServiceType;
import com.departamento.depa.repository.StayRepository;
import com.departamento.depa.repository.ServiceTypeRepository;
import com.departamento.depa.service.AdditionalServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                       @RequestParam(required = false) Long stayId,
                       Model model) {

        Page<AdditionalService> services = serviceService.findByFilters(stayId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("services", services);
        model.addAttribute("stayId", stayId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", services.getTotalPages());
        model.addAttribute("activePage", "services"); // ✅ Sidebar Active
        model.addAttribute("service", new AdditionalService());
        model.addAttribute("action", "create");

        return "admin/services";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AdditionalService service,
                       @RequestParam("stayId") Long stayId,
                       @RequestParam("serviceTypeId") Long serviceTypeId,
                       RedirectAttributes redirect) {
        try {
            // Vincular Estadía
            Stay stay = stayRepository.findById(stayId)
                    .orElseThrow(() -> new RuntimeException("Estadía no encontrada"));
            service.setStay(stay);

            // Vincular Tipo de Servicio
            ServiceType type = serviceTypeRepository.findById(serviceTypeId)
                    .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));
            service.setServiceType(type);

            // Fecha automática si es nuevo
            if (service.getFechaRegistro() == null) {
                service.setFechaRegistro(java.time.LocalDateTime.now());
            }

            serviceService.save(service);
            redirect.addFlashAttribute("msg", "Servicio registrado correctamente ✅");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        serviceService.deleteById(id);
        redirect.addFlashAttribute("msg", "Servicio eliminado 🗑️");
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceService.findById(id).orElseThrow());
        model.addAttribute("action", "edit");
        return "admin/services";
    }
}