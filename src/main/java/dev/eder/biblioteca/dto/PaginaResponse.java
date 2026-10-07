package dev.eder.biblioteca.dto;

import java.util.List;

import org.springframework.data.domain.Page;


// Respuesta paginada que sirve para CUALQUIER tipo.
// <T> es un "comodín": PaginaResponse<PrestamoResponse>, PaginaResponse<LibroResponse>, etc.
public record PaginaResponse<T>(
    List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas,
        boolean ultima
) {
    // Convierte el Page de Spring en nuestro DTO
    public static <T> PaginaResponse<T> de(Page<T> page) {
        return new PaginaResponse<>(
                page.getContent(),        // los elementos de esta página
                page.getNumber(),         // número de página (desde 0)
                page.getSize(),           // tamaño pedido
                page.getTotalElements(),  // total en toda la base
                page.getTotalPages(),     // total de páginas
                page.isLast()             // ¿es la última página?
        );
    }

}
