package dev.eder.biblioteca.exception;

// Se rompe una regla del negocio → 409
// Ej: sin stock, límite de préstamos, ya devuelto, ISBN repetido
public class ReglaNegocioException extends RuntimeException{
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

}
