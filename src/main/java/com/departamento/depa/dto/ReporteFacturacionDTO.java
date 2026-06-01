package com.departamento.depa.dto;

public class ReporteFacturacionDTO {
    private Long idReserva;
    private Long idUsuario;
    private Long idHabitacion;
    private Long idEstadia;
    private Long idServicio;

    public ReporteFacturacionDTO(Long idReserva, Long idUsuario, Long idHabitacion, Long idEstadia, Long idServicio) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idHabitacion = idHabitacion;
        this.idEstadia = idEstadia;
        this.idServicio = idServicio;
    }

    // Getters necesarios para Thymeleaf
    public Long getIdReserva() { return idReserva; }
    public Long getIdUsuario() { return idUsuario; }
    public Long getIdHabitacion() { return idHabitacion; }
    public Long getIdEstadia() { return idEstadia; }
    public Long getIdServicio() { return idServicio; }
}