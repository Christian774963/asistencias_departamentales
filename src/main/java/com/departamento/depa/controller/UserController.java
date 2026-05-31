package com.departamento.depa.controller;

import com.departamento.depa.entity.User;
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
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final RoomRepository roomRepository;

    public UserController(UserService userService, RoomRepository roomRepository) {
        this.userService = userService;
        this.roomRepository = roomRepository;
    }

    // LISTAR CON PAGINACIÓN (10 por página) Y ORDEN ASC
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String nombre,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String rol,
                       Model model) {

        // Normalizar strings vacíos
        nombre = (nombre == null || nombre.isBlank()) ? null : nombre;
        email = (email == null || email.isBlank()) ? null : email;
        rol = (rol == null || rol.isBlank() || "Todos".equals(rol)) ? null : rol;

        // Orden ASC (1, 2, 3...)
        Page<User> users = userService.findByFilters(
                nombre, email, rol,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        model.addAttribute("totalUsers", userService.count());      // Badge Usuarios
        model.addAttribute("totalRooms", roomRepository.count());      // Badge Habitaciones
        model.addAttribute("users", users);
        model.addAttribute("nombre", nombre);
        model.addAttribute("email", email);
        model.addAttribute("rol", rol);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalUsers", userService.count()); // Para el Badge del Sidebar
        model.addAttribute("user", new User()); // Objeto vacío para el modal Crear
        model.addAttribute("action", "create");
        model.addAttribute("activePage", "users");

        return "admin/users";
    }

    //  GUARDAR (Crear o Editar)
    @PostMapping("/save")
    public String save(@ModelAttribute User user, RedirectAttributes redirect) {
        // Si viene con ID es edición, si no es creación (JPA se encarga)
        userService.save(user);
        redirect.addFlashAttribute("msg", "Usuario guardado correctamente ");
        return "redirect:/admin/users";
    }

    //  ELIMINAR
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            // Verificar si el usuario tiene reservas
            if (userService.hasReservations(id)) {
                redirect.addFlashAttribute("error",
                        "No se puede eliminar: El usuario tiene reservas asociadas");
            } else {
                userService.deleteById(id);
                redirect.addFlashAttribute("msg", "Usuario eliminado correctamente");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error",
                    "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    //  EDITAR
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("action", "edit");
        // Limpiar filtros al editar
        model.addAttribute("nombre", "");
        model.addAttribute("email", "");
        model.addAttribute("rol", "");
        return "admin/users";
    }
}