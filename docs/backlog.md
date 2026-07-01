## 🛠️ Épico: Observabilidad y Monitoreo

### 📋 Tarea: Configurar Health Checks (Endpoints de Salud)
* **Descripción:** Implementar los endpoints estandarizados de salud para que el orquestador (Railway) pueda monitorear el estado de la aplicación.
* **Criterios de Aceptación:**
    - [ ] Instalar la extensión `quarkus-smallrye-health`.
    - [ ] Exponer los endpoints `/q/health/live` (Liveness) y `/q/health/ready` (Readiness).
    - [ ] Verificar que la base de datos PostgreSQL esté incluida en el chequeo de disponibilidad (Readiness).

### 📋 Tarea: Implementar Logging Estructurado en JSON
* **Descripción:** Cambiar el formato de los logs en producción de texto plano a JSON para facilitar su recolección y análisis.
* **Criterios de Aceptación:**
    - [ ] Configurar la extensión de logging de Quarkus para usar formato JSON (`quarkus.log.console.json=true`) mediante variables de entorno de producción.
    - [ ] Asegurar que se incluyan campos clave: `timestamp`, `level`, `traceId` (si aplica) y `message`.

### 📋 Tarea: Integrar OpenTelemetry para Tracing y Métricas Externas
* **Descripción:** Configurar OpenTelemetry para exportar trazas y métricas hacia un proveedor externo (ej. Grafana Cloud o Datadog) sin sobrecargar la instancia de Railway.
* **Criterios de Aceptación:**
    - [ ] Agregar la extensión `quarkus-opentelemetry`.
    - [ ] Configurar los endpoints del OTLP exporter mediante variables de entorno.
    - [ ] Verificar que las trazas de las peticiones HTTP y queries de SQL se envíen correctamente al proveedor externo.

---

## 🧪 Épico: Estrategia de Pruebas (Testing)

### 📋 Tarea: Implementar Pruebas de Integración (Base de Datos Real)
* **Descripción:** Desarrollar pruebas automatizadas de extremo a extremo para los endpoints del Expense Tracker usando una base de datos real.
* **Criterios de Aceptación:**
    - [ ] Configurar Quarkus Dev Services o Testcontainers para levantar PostgreSQL automáticamente durante la fase de test.
    - [ ] Crear pruebas con `REST-assured` para el flujo completo: Registrar usuario -> Login (obtener token) -> Crear gasto -> Validar persistencia.

### 📋 Tarea: Configurar Pruebas de Carga y Rendimiento (Performance Tests)
* **Descripción:** Crear scripts para simular usuarios concurrentes y evaluar la estabilidad del endpoint de resumen mensual.
* **Criterios de Aceptación:**
    - [ ] Escribir un script (usando k6 o Gatling) que simule tráfico concurrente (ej. 100-500 usuarios virtuales) en los endpoints de lectura y generación de reportes.
    - [ ] Medir tiempos de respuesta y tasas de error bajo carga.

---

## 🔒 Épico: Seguridad y Resiliencia

### 📋 Tarea: Implementar Rate Limiting en Endpoints Críticos
* **Descripción:** Proteger la API contra abuso, ataques de fuerza bruta o denegación de servicio (DoS), especialmente en Auth y reportes.
* **Criterios de Aceptación:**
    - [ ] Configurar un límite de peticiones (ej. máximo 5 peticiones por segundo por IP en `/auth/login`).
    - [ ] Retornar un código de estado `429 Too Many Requests` cuando se exceda el límite.

### 📋 Tarea: Estandarizar Manejo Global de Excepciones y Validación de DTOs
* **Descripción:** Asegurar que ninguna excepción interna exponga el StackTrace al cliente y que todos los datos de entrada estén estrictamente validados.
* **Criterios de Aceptación:**
    - [ ] Implementar un `ExceptionMapper` global para formatear los errores en un JSON limpio (`{ "error": "...", "status": ... }`).
    - [ ] Agregar anotaciones de Bean Validation (`@NotBlank`, `@Positive`) en los DTOs de entrada de creación de gastos y registros.

---

## 🚀 Épico: DevOps y Despliegue Continuo (CD)

### 📋 Tarea: Dockerización de la API (Imagen Optimizada/Nativa)
* **Descripción:** Crear el archivo Dockerfile final optimizado para el entorno de producción en Railway.
* **Criterios de Aceptación:**
    - [ ] Crear un `Dockerfile` multi-stage (puede ser la build nativa de Quarkus con GraalVM para minimizar consumo de RAM).
    - [ ] Asegurar que el contenedor corra con un usuario no-root por seguridad.

### 📋 Tarea: Externalizar Configuración mediante Variables de Entorno
* **Descripción:** Limpiar el archivo `application.properties` para que ninguna credencial quede hardcodeada.
* **Criterios de Aceptación:**
    - [ ] Mapear la conexión de Flyway y la base de datos a variables estándar: `${DB_HOST}`, `${DB_PORT}`, `${DB_NAME}`, `${DB_USERNAME}`, `${DB_PASSWORD}`.
    - [ ] Mapear llaves secretas de Auth (ej. secret keys de tokens).

### 📋 Tarea: Configurar Pipeline de Despliegue Continuo (CD) y Smoke Test
* **Descripción:** Automatizar el despliegue a Railway una vez que el pipeline de CI sea exitoso.
* **Criterios de Aceptación:**
    - [ ] Conectar el repositorio con Railway para disparar el deploy tras hacer push a `main` (o configurar un paso de deploy en GitHub Actions).
    - [ ] Agregar un paso final de Smoke Test: un script `curl` post-deploy que valide que `/q/health/live` responda exitosamente.
