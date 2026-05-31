package com.departamento.depa.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        // Obtenemos los roles del usuario autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Verificamos qué rol tiene y redirigimos accordingly
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_CLIENTE")) {
                return "redirect:/cliente/dashboard";
            }
        }

        // Por defecto (si algo sale mal), mandar a login
        return "redirect:/login";
    }

}