package com.departamento.depa.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        // TEXTO PLANO - Sin encriptación
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Comparación directa de texto plano
        return rawPassword.toString().equals(encodedPassword);
    }
}