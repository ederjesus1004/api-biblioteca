package dev.eder.biblioteca.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Lo que el cliente manda. NO tiene stockDisponible: lo maneja el sistema.
public record LibroRequest(
    @NotBlank (message = "El título es obligatorio")
        @Size (max = 150, message = "El título no puede tener más de 150 caracteres")
        String titulo,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 100, message = "El autor no puede tener más de 100 caracteres")
        String autor,

        @NotBlank(message = "El ISBN es obligatorio")
        @Pattern (regexp = "^\\d{13}$", message = "El ISBN debe tener exactamente 13 dígitos")
        String isbn,

        @NotNull (message = "El stock total es obligatorio")
        @Min (value = 1, message = "El stock total debe ser al menos 1")
        @Max (value = 100, message = "El stock total no puede ser mayor a 100")
        Integer stockTotal
) {

}
