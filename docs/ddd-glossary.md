# DDD Lite — Glosario de Conceptos Aplicados

Este documento registra los conceptos de DDD a medida que se aplican en el proyecto, con la definición y el ejemplo real usado en el código. No es teoría abstracta — cada entrada nace de una decisión real tomada durante el desarrollo.

---

## Value Object

**Definición:** objeto que se define por su valor, no por una identidad. No tiene `id`. Es inmutable. Encapsula validación y reglas sobre ese valor. Dos instancias con el mismo valor son iguales entre sí.

**Por qué importa:** garantiza que es imposible tener una instancia inválida en memoria — la validación ocurre en el único punto de construcción posible.

**Ejemplos en el proyecto:**
- `Email` — valida formato, normaliza a minúsculas (porque dos emails con distinto casing representan el mismo dato)
- `Password` — valida longitud (8-72 caracteres), **no normaliza** (cada carácter, incluyendo mayúsculas, es parte de la identidad real de una contraseña)
- `Money` (futuro) — encapsula `amount` + `currency`, porque viajan siempre juntos y comparten reglas

**Regla de implementación:**
- Constructor `private`
- Factory method estático público (`Email.of(...)`) — permite normalizar antes de validar, en un solo lugar
- `equals()`/`hashCode()` sobrescritos comparando el valor interno, no la referencia
- `toString()` sobrescrito — y con cuidado: en datos sensibles (`Password`) nunca debe exponer el valor real, ni siquiera en logs accidentales o stack traces

---

## Entidad (Entity)

**Definición:** objeto que se define por su identidad (`id`), no por sus atributos. Dos entidades con los mismos atributos pero distinto `id` son objetos distintos. Es mutable en los campos que representan estado que cambia con el tiempo, pero protege sus propias transiciones de estado.

**Ejemplo en el proyecto:** `User`

**Patrón de doble construcción — `register()` vs `reconstitute()`:**

Una entidad necesita dos rutas de creación distintas:
- **`register(...)`** → crea una instancia nueva. Genera su propio `id` (`UUID.randomUUID()`) y `createdAt` (`Instant.now()`). Usado cuando el caso de uso crea algo que no existía antes.
- **`reconstitute(...)`** → reconstruye una instancia existente a partir de datos ya persistidos. Recibe `id`, `createdAt`, etc. como parámetros porque ya existen. Usado por el repositorio al leer de la base de datos.

El constructor es siempre `private`. Esto fuerza que **toda** creación de la entidad pase por una de las dos rutas controladas, nunca por una construcción arbitraria con datos potencialmente inconsistentes.

**Predicados de dominio:** métodos que responden una pregunta de sí/no sobre el propio estado de la entidad, usando solo sus propios atributos — sin necesitar repositorio ni caso de uso externo.

- Ejemplo: `isDeleted()` (mira `deletedAt != null`), `isActive()` (negación legible de `isDeleted()`)
- Regla para decidir si un predicado pertenece a la entidad: **¿qué caso de uso real lo va a llamar?** Si no se puede nombrar uno concreto, es sobrediseño. (Ej: se descartó `isLoguedIn` y `isRegistered` por no corresponder a ningún campo real ni caso de uso.)

**Métodos que protegen invariantes (encapsulamiento real):**

En lugar de exponer setters públicos (`setDeletedAt(...)`), la entidad expone métodos de intención que validan la transición antes de aplicarla:

```java
public void delete() {
    if (isDeleted()) {
        throw new UserAlreadyDeletedException("...");
    }
    this.deletedAt = Instant.now();
}
```

Esto evita que cualquier código externo deje la entidad en un estado inválido (ej: eliminar dos veces, o "resucitar" seteando `deletedAt = null` desde afuera). La regla de negocio vive **una sola vez**, dentro del objeto que protege.

---

## Jerarquía de excepciones de dominio

**Problema que resuelve:** sin una jerarquía común, cada excepción de dominio extendería `RuntimeException` directamente, y el futuro `ExceptionMapper` (Quarkus) tendría que capturar cada una individualmente sin poder agruparlas por tipo de respuesta HTTP.

**Estructura:**

```
DomainException (abstracta)
├── ValidationException (abstracta) → mapea a 422 Unprocessable Entity
│   └── InvalidEmailException
│   └── InvalidPasswordException
└── ConflictException (abstracta) → mapea a 409 Conflict
    └── UserAlreadyExistsException
    └── UserAlreadyDeletedException
```

**Por qué las clases intermedias son `abstract`:** nadie debe poder lanzar un `ValidationException` o `ConflictException` genérico. Siempre se lanza una excepción específica y nombrada (`InvalidEmailException`), nunca la categoría. El compilador fuerza esa disciplina.

**Criterio para elegir 422 vs 409:**
- **422 (`ValidationException`)** → el dato en sí está mal formado o viola una regla semántica del dominio (formato de email inválido, longitud de password incorrecta). Es un problema del *contenido* enviado.
- **409 (`ConflictException`)** → el dato está bien formado, pero choca con el estado actual del sistema (email ya registrado, intentar eliminar algo ya eliminado). Es un problema de *estado*, no de formato.
- **400 (Bad Request)** → se reserva para errores estructurales del request (JSON malformado, campo requerido ausente) — ya cubierto automáticamente por Bean Validation (`@NotBlank`, etc.) en el `RequestDTO`, antes de que el caso de uso reciba el `Command`.

**Decisión YAGNI tomada:** el código HTTP está hardcodeado por nivel (`ValidationException` siempre 422, `ConflictException` siempre 409), no como campo abstracto parametrizable. Si en el futuro se necesita una excepción que rompa ese patrón, se migra puntualmente. No se sobre-diseña la jerarquía de antemano.

---

## Command, Result y DTO — separación de capas en el flujo de datos

**Problema que resuelve:** evitar que el caso de uso (capa `application`) conozca detalles de HTTP/JSON (capa `presentation`), y viceversa.

**Flujo:**

```
Cliente → JSON
  → Resource deserializa → RequestDTO (presentation)
  → Resource mapea       → Command (application)
  → UseCase ejecuta       → Result (application)
  → Resource mapea       → ResponseDTO (presentation)
  → Cliente ← JSON
```

- **RequestDTO** — conoce anotaciones de Jackson/Bean Validation. Vive en `presentation`.
- **Command** — objeto plano con los datos que el caso de uso necesita. No sabe nada de HTTP. Vive en `application`.
- **Result** — lo que el caso de uso devuelve internamente (ej: `AuthResult`, sin password). Vive en `application`.
- **ResponseDTO** — lo que se serializa de vuelta al cliente. Vive en `presentation`.

**Detalle de seguridad importante:** el `userId` en operaciones autenticadas **nunca** viene del `RequestDTO`/body — siempre se extrae del JWT en el `Resource` y se inyecta al `Command`. Si el cliente pudiera enviar su propio `userId` en el body, sería una vulnerabilidad de mass assignment / IDOR.

---

## IDOR (Insecure Direct Object Reference)

**Definición:** vulnerabilidad donde un usuario autenticado puede acceder a recursos de **otro** usuario simplemente conociendo o adivinando un ID, porque la query de búsqueda no valida ownership.

**Mitigación aplicada:** los métodos de repositorio que buscan un recurso específico de un usuario siempre reciben `userId` como parte del filtro, nunca solo el `id` del recurso.

```java
// Vulnerable a IDOR
Optional<Expense> findById(UUID id);

// Protegido
Optional<Expense> findByIdAndUserId(UUID id, UUID userId);
```

Si el recurso no pertenece al usuario autenticado, la query devuelve vacío (404), en lugar de devolver datos de otra persona.

---

## Proyección de dominio (vs DTO)

**Definición:** resultado de una query de agregación o lectura especializada que el repositorio devuelve directamente, sin pasar por la entidad completa. No es lo mismo que un DTO de presentación — vive en `application`, es el contrato entre el repositorio y el caso de uso.

**Ejemplo:** `ExpenseSummaryEntry` — resultado de `summarizeByMonth()`, que agrupa por categoría y moneda directamente en SQL (`GROUP BY`), evitando traer miles de filas de `Expense` a memoria para sumarlas ahí (mal uso de recursos, ver sección N+1/agregación más abajo).

---

## Agregación en base de datos vs en memoria

**Regla:** cuando un caso de uso necesita un resultado agregado (totales, sumas, agrupaciones), la agregación debe ejecutarse en la base de datos (`GROUP BY`, `SUM`), no traer todos los registros a memoria y agruparlos en Java.

**Por qué:** con un usuario con miles de registros, traer todas las filas a memoria para sumarlas es desperdiciar transferencia de red, memoria y CPU en algo que PostgreSQL hace de forma nativa y eficiente. La base de datos está diseñada exactamente para este tipo de operación.

---

## Cross-cutting concern

**Definición:** una responsabilidad que cruza múltiples módulos de negocio y no pertenece a ninguno en particular (logging, hashing de contraseñas, manejo global de excepciones).

**Ejemplo en el proyecto:** `PasswordHasher` — vive en `shared/infrastructure/security`, no dentro de `auth`, porque cualquier módulo futuro que maneje credenciales podría necesitarlo.

---

## Glosario rápido de términos usados

| Término | Significado |
|---|---|
| Factory method | Método estático que construye y valida un objeto, en lugar de un constructor público directo |
| Predicado de dominio | Método booleano que responde una pregunta sobre el propio estado de un objeto |
| Invariante | Una regla que siempre debe cumplirse para que un objeto sea válido (ej: un `User` eliminado no puede volver a eliminarse) |
| Reconstitute | Reconstruir una entidad a partir de datos ya persistidos, en contraste con crear una nueva |
| Cross-cutting concern | Funcionalidad transversal a múltiples módulos, vive en `shared` |
| IDOR | Insecure Direct Object Reference — acceso no autorizado a recursos ajenos vía manipulación de IDs |
