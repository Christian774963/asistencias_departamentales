package com.departamento.depa.service.impl;

import com.departamento.depa.entity.User;
import com.departamento.depa.repository.UserRepository;
import com.departamento.depa.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public Page<User> findByFilters(String nombre, String email, String rol, Pageable pageable) {
        // Normalizar parámetros vacíos a null
        nombre = (nombre == null || nombre.isBlank()) ? null : nombre;
        email = (email == null || email.isBlank()) ? null : email;
        rol = (rol == null || rol.isBlank() || "Todos".equals(rol)) ? null : rol;

        return userRepository.findByFilters(nombre, email, rol, pageable);
    }
}