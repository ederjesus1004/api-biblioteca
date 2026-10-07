package dev.eder.biblioteca.mapper;

import org.springframework.stereotype.Component;

import dev.eder.biblioteca.dto.LibroRequest;
import dev.eder.biblioteca.dto.LibroResponse;
import dev.eder.biblioteca.entity.Libro;

@Component
public class LibroMapper {

    // Solo copia los datos "simples". El stock lo maneja el service
    // porque tiene reglas (ver LibroService.actualizar).
    public void copiarDatos(LibroRequest request, Libro libro) {
        libro.setTitulo(request.titulo().trim());
        libro.setAutor(request.autor().trim());
        libro.setIsbn(request.isbn());
    }

    public LibroResponse toResponse(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getStockTotal(),
                libro.getStockDisponible()
        );
    }
}
