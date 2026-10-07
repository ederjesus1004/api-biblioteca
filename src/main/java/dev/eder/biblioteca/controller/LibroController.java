package dev.eder.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eder.biblioteca.dto.LibroRequest;
import dev.eder.biblioteca.dto.LibroResponse;
import dev.eder.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/libros")
@RequiredArgsConstructor 
public class LibroController {

    private final LibroService libroService;

    @GetMapping 
    public List<LibroResponse> listar() {
        return libroService.listar();
    }

    @GetMapping("/{id}")
    public LibroResponse obtenerPorId(@PathVariable Long id) {
        return libroService.obtenerPorId(id);
    }

    @PostMapping 
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(request));
    }

    @PutMapping ("/{id}")
    public LibroResponse actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.actualizar(id, request);
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
