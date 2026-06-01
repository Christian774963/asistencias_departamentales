package com.departamento.depa.controller;

import com.departamento.depa.entity.User;
import com.departamento.depa.repository.RoomRepository;
import com.departamento.depa.service.RoomService;
import com.departamento.depa.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
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
                       @RequestParam(required = false) Long id,
                       @RequestParam(required = false) String nombre,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) String rol,
                       Model model) {

        // Procesar parámetros para búsqueda (agregar % para LIKE)
        String nombreSearch = (nombre != null && !nombre.isEmpty()) ? "%" + nombre + "%" : null;
        String emailSearch = (email != null && !email.isEmpty()) ? "%" + email + "%" : null;

        Page<User> users = userService.findByFilters(id, nombreSearch, emailSearch, rol,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));

        model.addAttribute("users", users);
        model.addAttribute("id", id);
        model.addAttribute("nombre", nombre);  // Mantener valor original para el input
        model.addAttribute("email", email);
        model.addAttribute("rol", rol);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("activePage", "users");
        model.addAttribute("user", new User());
        model.addAttribute("action", "create");

        return "admin/users";
    }

    //  GUARDAR (Crear o Editar)
    @PostMapping("/save")
    public String save(@ModelAttribute User user, RedirectAttributes redirect) {
        try {
            // Validar email único (si es nuevo usuario o cambió el email)
            if (user.getId() == null || !userService.findById(user.getId()).get().getEmail().equals(user.getEmail())) {
                if (userService.findByEmail(user.getEmail()).isPresent()) {
                    redirect.addFlashAttribute("error", "El correo electrónico ya está registrado");
                    return "redirect:/admin/users";
                }
            }

            // Si es edición y no se proporcionó contraseña, mantener la actual
            if (user.getId() != null && (user.getPassword() == null || user.getPassword().trim().isEmpty())) {
                User existing = userService.findById(user.getId()).orElseThrow();
                user.setPassword(existing.getPassword());
            }

            userService.save(user);

            // MENSAJES DIFERENCIADOS POR ACCIÓN
            if (user.getId() == null) {
                redirect.addFlashAttribute("msg", "Usuario creado correctamente");
            } else {
                redirect.addFlashAttribute("msg", "Usuario actualizado correctamente");
            }

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.deleteById(id);
            redirect.addFlashAttribute("msg", "Usuario eliminado correctamente 🗑️");
        } catch (DataIntegrityViolationException e) {
            String mensajeError = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();

            if (mensajeError != null && mensajeError.contains("reservations_user_id_fkey")) {
                redirect.addFlashAttribute("error",
                        " No se puede eliminar: El cliente tiene " +
                                "reservas asociadas. Elimine primero las reservas.");
            } else if (mensajeError != null && mensajeError.contains("stays")) {
                redirect.addFlashAttribute("error",
                        " No se puede eliminar: El cliente tiene estadías registradas.");
            } else {
                redirect.addFlashAttribute("error",
                        " No se puede eliminar: El registro está siendo utilizado en otras tablas.");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
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