package dev.eder.biblioteca.service;

import dev.eder.biblioteca.dto.PaginaResponse;
import dev.eder.biblioteca.dto.PrestamoRequest;
import dev.eder.biblioteca.dto.PrestamoResponse;
import dev.eder.biblioteca.entity.EstadoPrestamo;
import dev.eder.biblioteca.entity.Libro;
import dev.eder.biblioteca.entity.Prestamo;
import dev.eder.biblioteca.exception.RecursoNoEncontradoException;
import dev.eder.biblioteca.exception.ReglaNegocioException;
import dev.eder.biblioteca.mapper.PrestamoMapper;
import dev.eder.biblioteca.repository.LibroRepository;
import dev.eder.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    // ===== Reglas del negocio como constantes =====
    private static final int DIAS_DE_PRESTAMO = 7;
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final PrestamoMapper prestamoMapper;

    // ===== Listado paginado con filtros opcionales =====
    @Transactional(readOnly = true)
    public PaginaResponse<PrestamoResponse> listar(EstadoPrestamo estado, String dni, Pageable pageable) {
        Page<PrestamoResponse> pagina = prestamoRepository.buscar(estado, dni, pageable)
                .map(prestamoMapper::toResponse);   // convierte cada préstamo de la página
        return PaginaResponse.de(pagina);
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtenerPorId(Long id) {
        return prestamoMapper.toResponse(buscarPrestamo(id));
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarVencidos() {
        return prestamoRepository.buscarVencidos(EstadoPrestamo.ACTIVO, LocalDate.now())
                .stream()
                .map(prestamoMapper::toResponse)
                .toList();
    }

    // ===== PRESTAR: toca dos tablas → @Transactional =====
    @Transactional
    public PrestamoResponse prestar(PrestamoRequest request) {
        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el libro con id " + request.libroId()));

        // Regla 5: debe haber copias disponibles
        if (libro.getStockDisponible() <= 0) {
            throw new ReglaNegocioException("No hay copias disponibles de \"" + libro.getTitulo() + "\"");
        }

        // Regla 6: máximo 3 préstamos activos por lector
        long activos = prestamoRepository.countByDniLectorAndEstado(request.dniLector(), EstadoPrestamo.ACTIVO);
        if (activos >= MAX_PRESTAMOS_ACTIVOS) {
            throw new ReglaNegocioException(
                    "El lector ya tiene " + MAX_PRESTAMOS_ACTIVOS + " préstamos activos");
        }

        // Regla 7: no puede tener este mismo libro prestado
        if (prestamoRepository.existsByDniLectorAndLibroIdAndEstado(
                request.dniLector(), libro.getId(), EstadoPrestamo.ACTIVO)) {
            throw new ReglaNegocioException("El lector ya tiene prestado este libro");
        }

        // Todo OK: armamos el préstamo
        Prestamo prestamo = prestamoMapper.toEntity(request, libro);

        // Regla 8: fechas y estado los pone el sistema
        LocalDate hoy = LocalDate.now();
        prestamo.setFechaPrestamo(hoy);
        prestamo.setFechaLimite(hoy.plusDays(DIAS_DE_PRESTAMO));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        // CAMBIO 1: el libro pierde una copia disponible (dirty checking lo guarda)
        libro.setStockDisponible(libro.getStockDisponible() - 1);

        // CAMBIO 2: guardamos el préstamo nuevo (es nuevo, aquí SÍ hace falta save)
        Prestamo guardado = prestamoRepository.save(prestamo);

        // Si cualquiera de los dos cambios falla, @Transactional deshace AMBOS
        return prestamoMapper.toResponse(guardado);
    }

    // ===== DEVOLVER: también toca dos tablas =====
    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = buscarPrestamo(id);

        // Regla 9: solo se devuelve lo que está activo
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ReglaNegocioException("El préstamo " + id + " ya fue devuelto");
        }

        // CAMBIO 1: el préstamo se cierra
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());

        // CAMBIO 2: el libro recupera una copia
        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);

        // Los dos objetos vinieron de la base → dirty checking guarda ambos. Sin save().
        return prestamoMapper.toResponse(prestamo);
    }

    private Prestamo buscarPrestamo(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el préstamo con id " + id));
    }
}