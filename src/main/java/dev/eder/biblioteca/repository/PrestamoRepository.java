package dev.eder.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.eder.biblioteca.entity.EstadoPrestamo;
import dev.eder.biblioteca.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long>{

     // ===== Para las reglas del negocio =====

    // ¿Cuántos préstamos ACTIVOS tiene este lector? (regla: máximo 3)
    long countByDniLectorAndEstado(String dniLector, EstadoPrestamo estado);

    // ¿Este lector ya tiene ESTE libro prestado y sin devolver? (regla: no repetir)
    boolean existsByDniLectorAndLibroIdAndEstado(String dnilector, Long libroId, EstadoPrestamo estado);

    // ¿El libro tiene préstamos? (regla: no eliminar)
    boolean existsByLibroId(Long libroId);

    // ===== Consultas personalizadas con @Query =====

    // Listado PAGINADO con dos filtros OPCIONALES
    @EntityGraph(attributePaths = "libro")
    @Query("""
            SELECT p FROM Prestamo p
            WHERE (:estado IS NULL OR p.estado = :estado)
              AND (:dni IS NULL OR p.dniLector = :dni)
            """)
    Page<Prestamo> buscar(@Param ("estado") EstadoPrestamo estado,
                          @Param("dni") String dni,
                          Pageable pageable);

    // Préstamos vencidos: siguen activos y la fecha límite ya pasó
    @EntityGraph(attributePaths = "libro")
    @Query("""
            SELECT p FROM Prestamo p
            WHERE p.estado = :estado
              AND p.fechaLimite < :hoy
            ORDER BY p.fechaLimite ASC
            """)
    List<Prestamo> buscarVencidos(@Param("estado") EstadoPrestamo estado,
                                  @Param("hoy") LocalDate hoy);

}
