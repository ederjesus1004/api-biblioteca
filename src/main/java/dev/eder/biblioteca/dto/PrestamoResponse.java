package dev.eder.biblioteca.dto;

import java.time.LocalDate;

import dev.eder.biblioteca.entity.EstadoPrestamo;

public record PrestamoResponse(
        Long id,
        Long libroId,
        String libroTitulo,
        String nombreLector,
        String dniLector,
        LocalDate fechaPrestamo,
        LocalDate fechaLimite,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado,
        boolean vencido            // ← CALCULADO: no existe en la tabla
) {

}
