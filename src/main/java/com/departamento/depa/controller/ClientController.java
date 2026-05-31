package com.departamento.depa.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClientController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        // Pasamos el nombre del usuario logueado a la vista
        model.addAttribute("userEmail", auth.getName());
        return "cliente/menu-cliente"; // Busca en templates/cliente/menu-cliente.html
    }
}