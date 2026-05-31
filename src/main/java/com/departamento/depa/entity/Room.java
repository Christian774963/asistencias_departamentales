package com.departamento.depa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Column(nullable = false, length = 20)
    private String estado; // Disponible, Ocupada, Mantenimiento
}