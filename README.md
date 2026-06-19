# Expense Tracker API

## Overview

Expense Tracker API es un proyecto con el que busco aprender y mejorar mi nivel técnico y teórico como desarrollador backend.

La razón de construirlo es usarlo como medio de aprendizaje para el desarrollo de APIs robustas y modernas, siguiendo buenas prácticas de desarrollo y seguridad.

Busco demostrar criterio técnico y pragmático en cuanto a las funcionalidades del negocio y del dominio, no simplemente construir features para que el proyecto se vea complejo o "bonito".

## Objetivo

Mis objetivos de aprendizaje a través de este proyecto son:

- **Java** — lenguaje robusto, con buena demanda laboral, que fuerza una base sólida de POO.
- **Quarkus** — framework eficiente para el desarrollo de APIs robustas y rápidas.
- **DDD (lite)** — entender conceptualmente qué es un Value Object, una Entidad y un Repositorio, y empezar a construir criterio técnico alrededor de esos conceptos, sin pretender cubrir DDD en toda su extensión.
- **Vertical Slice Architecture** — organizar el proyecto por funcionalidad de negocio en lugar de por capas técnicas.
- **Combinar ambos enfoques** — sacar lo mejor de DDD Lite y Vertical Slice, y aplicar ese criterio aquí y en proyectos futuros.
- **Hibernate y Panache** — el ORM para la persistencia de datos sobre PostgreSQL.

## Stack Tecnológico

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Quarkus | Framework backend |
| Hibernate ORM + Panache | Persistencia y mapeo objeto-relacional |
| PostgreSQL | Base de datos |
| Flyway | Migraciones de base de datos |
| SmallRye JWT | Autenticación y autorización |
| SmallRye OpenAPI | Documentación de la API (Swagger UI) |
| Hibernate Validator | Validación de datos |
| Docker | Contenerización |
| GitHub Actions | CI/CD |

## Arquitectura (resumen)

El proyecto sigue **Vertical Slice Architecture** combinada con **DDD Lite** y principios **hexagonales simplificados**.

En lugar de organizar el código por capas técnicas (`controllers/`, `services/`, `repositories/`), se organiza por capacidades de negocio: `auth/`, `categories/`, `expenses/`. Cada módulo contiene sus propias capas internas (`domain`, `application`, `infrastructure`, `presentation`), manteniendo el dominio independiente de Quarkus, Hibernate y Panache.

Las dependencias siempre apuntan hacia adentro: `presentation` depende de `application`, `application` depende de `domain`, e `infrastructure` implementa los contratos definidos en `domain`. Esto permite que la lógica de negocio se mantenga aislada de los detalles técnicos de persistencia y framework.

Para el diseño completo (entidades, Value Objects, contratos de repositorio, flujo de datos), ver [`docs/expense-tracker-api-planning.md`](docs/expense-tracker-api-planning.md).

## Estructura del Proyecto

```
src/main/java/com/michaelespinosa/expensetracker/
├── auth/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── categories/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── expenses/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
└── shared/
    ├── domain/
    └── infrastructure/
```

## Business Rules (resumen)

- Un gasto pertenece a exactamente un usuario y siempre debe tener una categoría asignada.
- Un gasto es editable solo dentro de las primeras 2 horas desde su creación.
- Los gastos nunca se eliminan; las categorías se eliminan con soft delete.
- Las categorías son globales y administradas por el sistema, no por el usuario.
- El resumen mensual de gastos agrupa por categoría y por moneda, sin conversión entre monedas.
- Sin autenticación no hay acceso a ningún recurso de la API.

Detalle completo de reglas de negocio en [`docs/expense-tracker-api-planning.md`](docs/expense-tracker-api-planning.md).

## API Endpoints (resumen)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Registra un nuevo usuario |
| `POST` | `/api/v1/auth/login` | Autentica y devuelve un JWT |
| `POST` | `/api/v1/auth/account/close` | Elimina la cuenta (requiere confirmación) |
| `GET` | `/api/v1/categories` | Lista categorías activas |
| `POST` | `/api/v1/expenses` | Registra un nuevo gasto |
| `GET` | `/api/v1/expenses` | Lista gastos con filtros |
| `GET` | `/api/v1/expenses/summary` | Resumen mensual por categoría y moneda |
| `GET` | `/api/v1/expenses/{id}` | Detalle de un gasto |
| `PUT` | `/api/v1/expenses/{id}` | Edita un gasto (ventana de 2 horas) |

Documentación interactiva disponible en Swagger UI una vez levantado el proyecto (`/q/swagger-ui`). Detalle completo en [`docs/expense-tracker-api-planning.md`](docs/expense-tracker-api-planning.md).

## Documentación

| Documento | Descripción |
|---|---|
| [Planning](docs/expense-tracker-api-planning.md) | Diseño completo: dominio, casos de uso, contratos, API |
| Definition of Done | _(pendiente)_ |
| Backlog | _(pendiente)_ |
| Naming Conventions | _(pendiente)_ |
| Git Flow | _(pendiente)_ |

## Estado Actual

🚧 **En desarrollo activo.**

Fase actual: planning y diseño de arquitectura completados. Implementación del código en progreso, comenzando por el módulo `auth`.

## Getting Started

### Requisitos

- JDK 21
- Docker (para PostgreSQL)
- Maven Wrapper incluido (`./mvnw`)

### Configuración

1. Clonar el repositorio
2. Crear un archivo `.env` en la raíz con las siguientes variables:
   ```
   POSTGRES_USER=
   POSTGRES_PASSWORD=
   POSTGRES_DB=expense_tracker
   JWT_SECRET=
   ```
3. Generar el par de llaves JWT:
   ```bash
   openssl genrsa -out src/main/resources/privateKey.pem 2048
   openssl rsa -in src/main/resources/privateKey.pem -pubout -out src/main/resources/publicKey.pem
   ```

### Ejecución en desarrollo

```bash
./mvnw quarkus:dev
```

La API estará disponible en `http://localhost:8080`, con Swagger UI en `http://localhost:8080/q/swagger-ui`.
