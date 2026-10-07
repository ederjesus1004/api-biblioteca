package dev.eder.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.eder.biblioteca.dto.LibroRequest;
import dev.eder.biblioteca.dto.LibroResponse;
import dev.eder.biblioteca.entity.Libro;
import dev.eder.biblioteca.exception.RecursoNoEncontradoException;
import dev.eder.biblioteca.exception.ReglaNegocioException;
import dev.eder.biblioteca.mapper.LibroMapper;
import dev.eder.biblioteca.repository.LibroRepository;
import dev.eder.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final LibroMapper libroMapper;

    @Transactional(readOnly = true)
    public List<LibroResponse> listar() {
        return libroRepository.findAll()
                .stream()
                .map(libroMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse obtenerPorId(Long id) {
        return libroMapper.toResponse(buscarLibro(id));
    }

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        // Regla 1: ISBN único
        if (libroRepository.existsByIsbn(request.isbn())) {
            throw new ReglaNegocioException("Ya existe un libro con el ISBN " + request.isbn());
        }

        Libro libro = new Libro();
        libroMapper.copiarDatos(request, libro);

        // Regla 2: al crear, todas las copias están disponibles
        libro.setStockTotal(request.stockTotal());
        libro.setStockDisponible(request.stockTotal());

        return libroMapper.toResponse(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse actualizar(Long id, LibroRequest request) {
        Libro libro = buscarLibro(id);

        // Regla 1: ISBN único (sin contar este mismo libro)
        if (libroRepository.existsByIsbnAndIdNot(request.isbn(), id)) {
            throw new ReglaNegocioException("Ya existe un libro con el ISBN " + request.isbn());
        }

        // Regla 3: el nuevo total no puede ser menor que las copias prestadas
        int prestados = libro.getStockTotal() - libro.getStockDisponible();
        if (request.stockTotal() < prestados) {
            throw new ReglaNegocioException(
                    "No puedes dejar el stock en " + request.stockTotal()
                            + " porque hay " + prestados + " copia(s) prestada(s)");
        }

        libroMapper.copiarDatos(request, libro);
        libro.setStockTotal(request.stockTotal());
        libro.setStockDisponible(request.stockTotal() - prestados);   // recalculamos

        // Sin save(): dirty checking guarda los cambios al terminar el método
        return libroMapper.toResponse(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = buscarLibro(id);

        // Regla 4: no se elimina si tiene préstamos (perderíamos el historial)
        if (prestamoRepository.existsByLibroId(id)) {
            throw new ReglaNegocioException("No se puede eliminar el libro porque tiene préstamos registrados");
        }

        libroRepository.delete(libro);
    }

    private Libro buscarLibro(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el libro con id " + id));
    }
}
