package dev.eder.biblioteca.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import dev.eder.biblioteca.dto.PrestamoRequest;
import dev.eder.biblioteca.dto.PrestamoResponse;
import dev.eder.biblioteca.entity.EstadoPrestamo;
import dev.eder.biblioteca.entity.Libro;
import dev.eder.biblioteca.entity.Prestamo;

@Component 
public class PrestamoMapper {

    // Request + libro ya buscado → préstamo nuevo (sin fechas ni estado: eso lo pone el service)
    public Prestamo toEntity(PrestamoRequest request, Libro libro) {
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setNombreLector(request.nombreLector().trim());
        prestamo.setDniLector(request.dniLector());
        return prestamo;
    }

    public PrestamoResponse toResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getLibro().getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getNombreLector(),
                prestamo.getDniLector(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaLimite(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado(),
                estaVencido(prestamo)          // ← campo calculado
        );
    }

    // Vencido = sigue activo Y la fecha límite ya pasó
    private boolean estaVencido(Prestamo prestamo) {
        return prestamo.getEstado() == EstadoPrestamo.ACTIVO
                && prestamo.getFechaLimite().isBefore(LocalDate.now());
    }

}
