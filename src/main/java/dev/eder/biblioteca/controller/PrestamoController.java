package dev.eder.biblioteca.controller;

import dev.eder.biblioteca.dto.PaginaResponse;
import dev.eder.biblioteca.dto.PrestamoRequest;
import dev.eder.biblioteca.dto.PrestamoResponse;
import dev.eder.biblioteca.entity.EstadoPrestamo;
import dev.eder.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/prestamos")
@RequiredArgsConstructor 
public class PrestamoController {

    private final PrestamoService prestamoService;

    // GET /api/prestamos?estado=ACTIVO&dni=12345678&page=0&size=10&sort=fechaPrestamo,desc
    // Todos los parámetros son opcionales
    @GetMapping 
    public PaginaResponse<PrestamoResponse> listar(
            @RequestParam (required = false) EstadoPrestamo estado,
            @RequestParam(required = false) String dni,
            @PageableDefault (size = 10, sort = "fechaPrestamo", direction = Sort.Direction.DESC) Pageable pageable) {
        return prestamoService.listar(estado, dni, pageable);
    }

    // OJO: esta ruta va ANTES que /{id} en importancia, Spring la distingue sola
    // porque "vencidos" no es un número
    @GetMapping("/vencidos")
    public List<PrestamoResponse> listarVencidos() {
        return prestamoService.listarVencidos();
    }

    @GetMapping("/{id}")
    public PrestamoResponse obtenerPorId(@PathVariable Long id) {
        return prestamoService.obtenerPorId(id);
    }

    // POST /api/prestamos → prestar un libro
    @PostMapping
    public ResponseEntity<PrestamoResponse> prestar(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.prestar(request));
    }

    // PATCH /api/prestamos/5/devolver → acción sobre el préstamo 5, SIN body
    @PatchMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }
}
