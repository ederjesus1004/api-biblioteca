package dev.eder.biblioteca.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 150)
    private String autor;

    // ISBN de 13 dígitos, único
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    // Cuántas copias tiene la biblioteca en total
    @Column(name = "stock_total", nullable = false)
    private Integer stockTotal;

    // Cuántas copias están en el estante ahora (no prestadas)
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;


}
