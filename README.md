# API de Biblioteca · Libros y Préstamos

API REST para gestionar libros y sus préstamos, con control de stock, reglas de negocio, paginación y consultas con `@Query`. La hice para practicar transacciones, acciones con PATCH y lógica de negocio en el service.

## Tecnologías

Java 21 · Spring Boot · Spring Data JPA · Spring Validation · PostgreSQL · Lombok

## Reglas de negocio

**Libros**
- ISBN único de 13 dígitos
- Al crear, el stock disponible es igual al total
- No se puede bajar el stock total por debajo de las copias prestadas
- No se puede eliminar un libro con préstamos registrados

**Préstamos**
- Solo se presta si hay copias disponibles
- Máximo 3 préstamos activos por lector
- Un lector no puede tener el mismo libro prestado dos veces
- Duración de 7 días, calculada por el sistema
- Prestar y devolver actualizan el stock en la misma transacción
- `vencido` se calcula al consultar: préstamo activo con fecha límite pasada

## Cómo ejecutarlo

1. Crear la base de datos: `CREATE DATABASE biblioteca_db;`
2. Revisar usuario y contraseña en `src/main/resources/application.properties` (por defecto `postgres` / `postgres`)
3. Ejecutar:

```bash
git clone https://github.com/ederjesus1004/api-biblioteca.git
cd api-biblioteca
./mvnw spring-boot:run
```

En Windows: `mvnw.cmd spring-boot:run`. La API queda en `http://localhost:8080`.

## Endpoints

### Libros

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/api/libros` | 200 |
| GET | `/api/libros/{id}` | 200 / 404 |
| POST | `/api/libros` | 201 / 400 / 409 |
| PUT | `/api/libros/{id}` | 200 / 400 / 404 / 409 |
| DELETE | `/api/libros/{id}` | 204 / 404 / 409 |

### Préstamos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/prestamos?estado=&dni=&page=0&size=10` | Listado paginado con filtros opcionales |
| GET | `/api/prestamos/vencidos` | Préstamos activos con fecha límite pasada |
| GET | `/api/prestamos/{id}` | Detalle de un préstamo |
| POST | `/api/prestamos` | Registrar un préstamo |
| PATCH | `/api/prestamos/{id}/devolver` | Registrar la devolución |

## Ejemplo: prestar un libro

`POST /api/prestamos`

```json
{
  "libroId": 1,
  "nombreLector": "Eder Cuaresma",
  "dniLector": "12345678"
}
```

Respuesta `201`:

```json
{
  "id": 1,
  "libroId": 1,
  "libroTitulo": "Cien años de soledad",
  "nombreLector": "Eder Cuaresma",
  "dniLector": "12345678",
  "fechaPrestamo": "2026-10-05",
  "fechaLimite": "2026-10-12",
  "fechaDevolucion": null,
  "estado": "ACTIVO",
  "vencido": false
}
```

## Códigos de respuesta

| Código | Cuándo sale |
|--------|-------------|
| 400 | Datos inválidos, filtro o parámetro de orden incorrecto |
| 404 | El libro o el préstamo no existen |
| 409 | Se rompe una regla de negocio (sin stock, límite de préstamos, ya devuelto, ISBN repetido...) |

## Lo que aprendí

- Qué hace `@Transactional` cuando una operación cambia dos tablas
- Cuándo Hibernate guarda los cambios sin llamar a `save()` (dirty checking)
- Paginación con `Pageable` y una respuesta paginada propia
- `@Query` con filtros opcionales
- Acciones con `PATCH` en vez de editar con `PUT`
- Campos calculados en el DTO que no existen en la tabla

## Autor

**Eder Cuaresma** · [GitHub](https://github.com/ederjesus1004)