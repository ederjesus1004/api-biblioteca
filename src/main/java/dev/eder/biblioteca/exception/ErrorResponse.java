package dev.eder.biblioteca.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    LocalDateTime fecha,
        int estado,
        String mensaje,
        Map<String, String> errores
) {

}
