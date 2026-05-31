package com.departamento.depa.controller;

import com.departamento.depa.entity.Room;
import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.service.RoomService;
import com.departamento.depa.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final UserService userService;// ✅ Declarar

    // ✅ Inyectar ambos en el constructor
    public RoomController(RoomService roomService, RoomRepository roomRepository,UserService userService) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String numero,
                       @RequestParam(required = false) String tipo,
                       @RequestParam(required = false) String estado,
                       Model model) {

        numero = (numero == null || numero.isBlank()) ? null : numero;
        tipo = (tipo == null || tipo.isBlank() || "Todos".equals(tipo)) ? null : tipo;
        estado = (estado == null || estado.isBlank() || "Todos".equals(estado)) ? null : estado;

        Page<Room> rooms = roomService.findByFilters(
                numero, tipo, estado,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        model.addAttribute("rooms", rooms);
        model.addAttribute("numero", numero);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estado", estado);
        model.addAttribute("totalUsers", userService.count());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rooms.getTotalPages());
        model.addAttribute("totalRooms", roomRepository.count());
        model.addAttribute("room", new Room());
        model.addAttribute("action", "create");
        model.addAttribute("activePage", "rooms");

        return "admin/rooms";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Room room, RedirectAttributes redirect) {
        roomService.save(room);
        redirect.addFlashAttribute("msg", "Habitación guardada correctamente ✅");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        roomService.deleteById(id);
        redirect.addFlashAttribute("msg", "Habitación eliminada 🗑️");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Room room = roomService.findById(id).orElseThrow();
        model.addAttribute("room", room);
        model.addAttribute("action", "edit");
        model.addAttribute("numero", "");
        model.addAttribute("tipo", "");
        model.addAttribute("estado", "");
        return "admin/rooms";
    }
}