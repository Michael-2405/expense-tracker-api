# Expense Tracker API — Planning Document

## 1. Reglas de Negocio

### User
- Un usuario se identifica por email único en el sistema
- La contraseña se valida en dominio (formato) y se hashea en infraestructura
- Un usuario puede eliminar su cuenta con confirmación de email + contraseña
- Al eliminar la cuenta, todos sus gastos se eliminan en cascada (hard delete)
- Las cuentas se eliminan con soft delete (`deletedAt`)
- Access token JWT con duración de 24 horas
- Sin refresh tokens en esta versión (mejora futura documentada)
- Revocación: el cliente descarta el token al cerrar sesión;
  el token sigue válido hasta expiración natural

### Category
- Las categorías son globales, administradas por el sistema
- El usuario no puede crear ni eliminar categorías
- Las categorías se eliminan con soft delete (`deletedAt`)
- Un gasto puede referenciar una categoría con soft delete (el gasto permanece)
- Solo se exponen categorías activas (`deletedAt IS NULL`) al usuario

### Expense
- Un gasto pertenece a exactamente un usuario
- La categoría es obligatoria al registrar un gasto
- Un gasto es editable solo dentro de las primeras 2 horas desde `createdAt`
- Los gastos nunca se eliminan físicamente (sin hard delete, sin soft delete)
- Los filtros de listado operan sobre `createdAt`, no sobre `expenseDate`
- `expenseDate` existe como información descriptiva, no como criterio de consulta
- El resumen mensual agrupa por moneda, no convierte entre monedas
- `updatedAt` en Expense es metadata interna, nunca se expone al cliente

---

## 2. Modelo del Dominio

### Entidades

**User**
```
id           UUID
firstName    String
lastName     String
email        Email        (Value Object)
password     Password     (Value Object)
createdAt    Instant
updatedAt    Instant
deletedAt    Instant
```

**Category**
```
id           UUID
name         String
color        Color        (Value Object)
createdAt    Instant
updatedAt    Instant
deletedAt    Instant
```

**Expense**
```
id           UUID
title        String
cost         Money        (Value Object → amount + currency)
expenseDate  LocalDate
userId       UUID
categoryId   UUID
createdAt    Instant
updatedAt    Instant      (interno, nunca expuesto)
```

### Value Objects

| Value Object | Responsabilidad |
|---|---|
| `Email` | Valida formato de email |
| `Password` | Valida longitud mínima y caracteres requeridos |
| `Color` | Valida formato hex (`#RRGGBB`) |
| `Money` | Encapsula `amount` (> 0) + `currency` (enum) |
| `Currency` | Enum: `USD`, `EUR`, `DOP` |

### Comportamiento en entidades

```java
// En Expense — regla de negocio de editabilidad
public boolean isEditable() {
    return createdAt.isAfter(Instant.now().minus(2, ChronoUnit.HOURS));
}
```

---

## 3. Casos de Uso

### Auth
| Caso de uso | Descripción |
|---|---|
| `RegisterUser` | Registra un nuevo usuario |
| `LoginUser` | Autentica y devuelve JWT |
| `DeleteAccount` | Elimina cuenta con confirmación email + contraseña |

### Categories
| Caso de uso | Descripción |
|---|---|
| `ListActiveCategories` | Lista categorías con `deletedAt IS NULL` |

### Expenses
| Caso de uso | Descripción |
|---|---|
| `CreateExpense` | Registra un nuevo gasto |
| `EditExpense` | Edita un gasto dentro de la ventana de 2 horas |
| `ListExpenses` | Lista gastos del usuario con filtros |
| `GetExpenseDetail` | Devuelve el detalle de un gasto específico |
| `GetMonthlySummary` | Resumen de gastos agrupado por categoría y moneda |

---

## 4. Contratos de Repositorio

### UserRepository
```java
public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    void delete(User user);
}
```

### CategoryRepository
```java
public interface CategoryRepository {
    List<Category> findAllActive();
    Optional<Category> findActiveById(UUID id);
}
```

### ExpenseRepository
```java
public interface ExpenseRepository {
    void save(Expense expense);
    Optional<Expense> findByIdAndUserId(UUID id, UUID userId);
    List<Expense> findByUserIdAndFilters(UUID userId, ExpenseFilter filter);
    List<ExpenseSummaryEntry> summarizeByMonth(UUID userId, int year, int month);
    void deleteAllByUserId(UUID userId);
}
```

**Notas:**
- `findByIdAndUserId` previene IDOR (Insecure Direct Object Reference)
- `findAllActive` y `findActiveById` filtran `deletedAt IS NULL` internamente
- `ExpenseSummaryEntry` es una proyección de dominio, vive en `application`
- `ExpenseFilter` vive en `application`, no en `presentation`
- `summarizeByMonth` ejecuta agregación en base de datos (no en memoria)

---

## 5. Estructura de Paquetes

```
src/main/java/com/michael/expensetracker/
│
├── shared/
│   ├── domain/
│   │   └── exception/
│   │       ├── DomainException.java
│   │       └── ValidationException.java
│   └── infrastructure/
│       └── security/
│           └── PasswordHasher.java
│
├── auth/
│   ├── domain/
│   │   ├── model/
│   │   │   └── User.java
│   │   ├── valueobject/
│   │   │   ├── Email.java
│   │   │   └── Password.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   └── exception/
│   │       ├── UserAlreadyExistsException.java
│   │       └── InvalidCredentialsException.java
│   ├── application/
│   │   ├── command/
│   │   │   ├── RegisterUserCommand.java
│   │   │   ├── LoginUserCommand.java
│   │   │   └── DeleteAccountCommand.java
│   │   ├── result/
│   │   │   └── AuthResult.java
│   │   └── usecase/
│   │       ├── RegisterUser.java
│   │       ├── LoginUser.java
│   │       └── DeleteAccount.java
│   ├── infrastructure/
│   │   └── repository/
│   │       └── PanacheUserRepository.java
│   └── presentation/
│       ├── AuthResource.java
│       ├── request/
│       │   ├── RegisterUserRequest.java
│       │   ├── LoginRequest.java
│       │   └── DeleteAccountRequest.java
│       └── response/
│           └── AuthResponse.java
│
├── categories/
│   ├── domain/
│   │   ├── model/
│   │   │   └── Category.java
│   │   ├── valueobject/
│   │   │   └── Color.java
│   │   ├── repository/
│   │   │   └── CategoryRepository.java
│   │   └── exception/
│   │       └── CategoryNotFoundException.java
│   ├── application/
│   │   ├── result/
│   │   │   └── CategoryResult.java
│   │   └── usecase/
│   │       └── ListActiveCategories.java
│   ├── infrastructure/
│   │   └── repository/
│   │       └── PanacheCategoryRepository.java
│   └── presentation/
│       ├── CategoryResource.java
│       └── response/
│           └── CategoryResponse.java
│
└── expenses/
    ├── domain/
    │   ├── model/
    │   │   └── Expense.java
    │   ├── valueobject/
    │   │   ├── Money.java
    │   │   └── Currency.java
    │   ├── repository/
    │   │   └── ExpenseRepository.java
    │   └── exception/
    │       ├── ExpenseNotFoundException.java
    │       └── ExpenseNotEditableException.java
    ├── application/
    │   ├── command/
    │   │   ├── CreateExpenseCommand.java
    │   │   └── EditExpenseCommand.java
    │   ├── result/
    │   │   ├── ExpenseResult.java
    │   │   └── ExpenseSummaryEntry.java
    │   ├── filter/
    │   │   └── ExpenseFilter.java
    │   └── usecase/
    │       ├── CreateExpense.java
    │       ├── EditExpense.java
    │       ├── ListExpenses.java
    │       ├── GetExpenseDetail.java
    │       └── GetMonthlySummary.java
    ├── infrastructure/
    │   └── repository/
    │       └── PanacheExpenseRepository.java
    └── presentation/
        ├── ExpenseResource.java
        ├── request/
        │   ├── CreateExpenseRequest.java
        │   └── EditExpenseRequest.java
        └── response/
            ├── ExpenseResponse.java
            └── ExpenseSummaryResponse.java
```

---

## 6. API REST

### Base URL
```
/api/v1
```

### Auth
| Método | Path | Auth | Body | Response |
|---|---|---|---|---|
| `POST` | `/auth/register` | No | `firstName, lastName, email, password` | `201` |
| `POST` | `/auth/login` | No | `email, password` | `200 + token` |
| `POST` | `/auth/account/close` | Sí | `email, password` | `200` |

### Categories
| Método | Path | Auth | Response |
|---|---|---|---|
| `GET` | `/categories` | Sí | `200 + List<CategoryResponse>` |

### Expenses
| Método | Path | Auth | Body/Params | Response |
|---|---|---|---|---|
| `POST` | `/expenses` | Sí | `title, cost, expenseDate, categoryId` | `201` |
| `GET` | `/expenses` | Sí | query params | `200 + List<ExpenseResponse>` |
| `GET` | `/expenses/summary` | Sí | `?year&month` | `200 + SummaryResponse` |
| `GET` | `/expenses/{id}` | Sí | — | `200 + ExpenseResponse` |
| `PUT` | `/expenses/{id}` | Sí | campos editables | `200 + ExpenseResponse` |

### Query params de `GET /expenses`
```
?period=LAST_WEEK|LAST_MONTH|LAST_3_MONTHS
?startDate=2024-01-01
?endDate=2024-01-31
?categoryId=uuid
```
**Regla:** `period` y `startDate/endDate` son mutuamente excluyentes. Si se envían ambos → `400 Bad Request`.

### Header de autenticación
```
Authorization: Bearer {token}
```

---

## 7. Flujo de datos por request

```
Cliente
  → JSON request
  → Resource (deserializa RequestDTO, extrae userId del JWT)
  → Command
  → UseCase (lógica de negocio)
  → Repository (persistencia / consulta)
  → Result
  → Resource (mapea a ResponseDTO)
  → JSON response
  → Cliente
```

---

## 8. Principios y decisiones transversales

- **IDOR prevention:** siempre filtrar por `userId` al buscar recursos del usuario
- **Soft delete:** `Category` y `User` usan `deletedAt`, `Expense` nunca se elimina
- **Agregación en DB:** `GetMonthlySummary` ejecuta `GROUP BY` en PostgreSQL, no en memoria
- **JWT:** el `userId` siempre viene del token, nunca del request body
- **Monedas:** sin conversión, el resumen agrupa por moneda (`USD`, `EUR`, `DOP`)
- **Inmutabilidad de gastos:** editable solo en ventana de 2 horas desde `createdAt`
- **Nombres de métodos:** revelan intención (`findAllActive`, `findByIdAndUserId`)
- **YAGNI:** sin typed IDs por ahora, se refactoriza si surge la necesidad
- **PasswordHasher:** cross-cutting concern en `shared`, hashing solo en infraestructura
- **Rutas estáticas primero:** JAX-RS resuelve `/expenses/summary` antes que `/expenses/{id}`
