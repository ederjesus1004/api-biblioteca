package dev.eder.biblioteca.entity;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     // Muchos préstamos son del mismo libro
    @ManyToOne(fetch = FetchType.LAZY, optional =false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(name = "nombre_lector", nullable = false, length = 100)
    private String nombreLector;

    @Column(name = "dni_lector", nullable = false, length = 8)
    private String dniLector;

    // LocalDate = solo fecha, sin hora
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_limite", nullable = false)
    private LocalDate fechaLimite;

    // Vacía mientras no se devuelva
    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado;
}
