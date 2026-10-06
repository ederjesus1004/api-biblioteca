package dev.eder.biblioteca.entity;

// Estados que puede tener un préstamo.
// "Vencido" NO es un estado: se calcula según la fecha (ver PrestamoMapper).
public enum EstadoPrestamo {

    ACTIVO,
    DEVUELTO
}
