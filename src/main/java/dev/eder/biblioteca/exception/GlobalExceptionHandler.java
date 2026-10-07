package dev.eder.biblioteca.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice   
public class GlobalExceptionHandler {

    @ExceptionHandler (RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return armarRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(ReglaNegocioException ex) {
        return armarRespuesta(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return armarRespuesta(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return armarRespuesta(HttpStatus.BAD_REQUEST, "El JSON enviado no es válido", Map.of());
    }

    // ?estado=PERDIDO o /prestamos/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return armarRespuesta(HttpStatus.BAD_REQUEST,
                "El valor '" + ex.getValue() + "' no es válido para " + ex.getName(), Map.of());
    }

    // NUEVO: ?sort=campoQueNoExiste
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> manejarOrdenInvalido(PropertyReferenceException ex) {
        return armarRespuesta(HttpStatus.BAD_REQUEST,
                "No se puede ordenar por '" + ex.getPropertyName() + "'", Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception ex) {
        log.error("Error inesperado", ex);
        return armarRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", Map.of());
    }

    private ResponseEntity<ErrorResponse> armarRespuesta(HttpStatus status, String mensaje,
                                                         Map<String, String> errores) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(LocalDateTime.now(), status.value(), mensaje, errores));
    }
}
