package dev.eder.biblioteca.dto;

// Lo que el cliente ve. SÍ muestra stockDisponible (puede verlo, no cambiarlo).
public record LibroResponse(
    Long id,
        String titulo,
        String autor,
        String isbn,
        Integer stockTotal,
        Integer stockDisponible
) {

}
