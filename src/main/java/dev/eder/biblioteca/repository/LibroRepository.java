package dev.eder.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.eder.biblioteca.entity.Libro;

public interface LibroRepository extends JpaRepository<Libro,Long>{

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndNot(String isbn,Long id);

}
