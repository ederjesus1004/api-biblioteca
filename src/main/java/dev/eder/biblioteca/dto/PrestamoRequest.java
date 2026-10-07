package dev.eder.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// Para prestar solo hace falta: qué libro y quién lo lleva.
// Fechas y estado los pone el sistema.
public record PrestamoRequest(
        @NotNull(message = "El libro es obligatorio")
        @Positive (message = "El id del libro debe ser mayor a 0")
        Long libroId,

        @NotBlank(message = "El nombre del lector es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String nombreLector,

        @NotBlank(message = "El DNI del lector es obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
        String dniLector
) {

}
