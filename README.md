# Gestor de Proyectos (API tipo Trello)

API REST para gestionar proyectos colaborativos tipo Trello: tableros (proyectos), columnas (listas) y tarjetas (tareas). Incluye autenticación JWT y roles por proyecto (OWNER / MEMBER).

## Tabla de contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Controladores y endpoints](#controladores-y-endpoints)
- [Modelo de datos](#modelo-de-datos)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Seguridad](#seguridad)
- [Documentación Swagger](#documentación-swagger)

---

## Tecnologías

| Tecnología        | Uso                          |
|-------------------|-----------------------------|
| **Java 21**       | Lenguaje                    |
| **Spring Boot 3.2** | Framework web + seguridad |
| **Spring Data JPA** | Persistencia               |
| **Spring Security + JWT** | Autenticación stateless |
| **Oracle Database** | Base de datos (Docker)   |
| **SpringDoc OpenAPI** | Documentación Swagger UI |
| **Maven**         | Build y dependencias        |
| **Lombok**        | Reducción de boilerplate    |
| **Jakarta Validation** | Validación de DTOs   |

---

## Arquitectura

La aplicación usa una **arquitectura en capas** con separación clara de responsabilidades


**Otras capas:**

- **DTO**: Objetos de entrada/salida (CreateProjectRequest, ProjectDTO, AuthResponse, etc.).
- **Security**: JwtUtil, JwtAuthenticationFilter, UserPrincipal — validación del token y contexto de seguridad.
- **Config**: SecurityConfig (rutas públicas/protegidas, CORS), SwaggerConfig.
- **Exception**: GlobalExceptionHandler (@ControllerAdvice), NoSuchResourceFoundException (404), BadResourceRequestException (400).

---

## Controladores y endpoints

Base URL: `http://localhost:8080/api/v1`

Las rutas salvo `/auth/**` requieren cabecera: `Authorization: Bearer <token>`.

### 1. AuthController — `/api/v1/auth`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/register` | Registrar usuario (email, password, nombre). Devuelve JWT. |
| POST | `/auth/login` | Login (email, password). Devuelve JWT. |

**Público** (no requiere token).

---

### 2. ProjectController — `/api/v1/projects`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/projects` | Crear proyecto (te añade como OWNER). |
| GET | `/projects` | Listar proyectos del usuario. |
| GET | `/projects/{id}` | Ver proyecto con listas y tarjetas. |
| PUT | `/projects/{id}` | Actualizar nombre/descripción. |
| DELETE | `/projects/{id}` | Eliminar proyecto (solo OWNER). |
| POST | `/projects/{id}/members` | Añadir miembro por email (solo OWNER). |
| DELETE | `/projects/{projectId}/members/{userId}` | Quitar miembro (solo OWNER). |

---

### 3. BoardListController — `/api/v1/projects/{projectId}/lists`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/projects/{projectId}/lists` | Crear lista (columna). |
| GET | `/projects/{projectId}/lists` | Listar listas del proyecto con tarjetas. |
| PUT | `/projects/{projectId}/lists/{listId}` | Actualizar lista. |
| DELETE | `/projects/{projectId}/lists/{listId}` | Eliminar lista. |

---

### 4. CardController — `/api/v1/lists/{listId}/cards`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/lists/{listId}/cards` | Crear tarjeta. |
| GET | `/lists/{listId}/cards` | Listar tarjetas de la lista. |
| PUT | `/lists/{listId}/cards/{cardId}` | Actualizar tarjeta. |
| POST | `/lists/{listId}/cards/{cardId}/move` | Mover tarjeta a otra lista (mismo proyecto). |
| DELETE | `/lists/{listId}/cards/{cardId}` | Eliminar tarjeta. |

---

## Modelo de datos

| Entidad | Tabla | Descripción |
|---------|--------|-------------|
| **User** | `app_user` | Usuario (email, password, nombre, rol USER/ADMIN). |
| **Project** | `project` | Tablero (nombre, descripción, creador, fechas). |
| **ProjectMember** | `project_member` | Relación proyecto–usuario con rol OWNER o MEMBER. |
| **BoardList** | `board_list` | Columna del tablero (título, posición, proyecto). |
| **Card** | `card` | Tarjeta (título, descripción, posición, lista, asignado, fecha límite). |

**Relaciones:** Project → BoardList → Card. User es creador de proyectos, miembro (ProjectMember) y asignado de tarjetas.

**Permisos por rol en el proyecto:**

- **OWNER**: eliminar proyecto, añadir y quitar miembros.
- **OWNER y MEMBER**: ver/editar proyecto, listas y tarjetas.

---

## Configuración y ejecución

### Requisitos

- Java 21
- Maven 3.6+
- Docker (para Oracle)

### Base de datos (Docker)

```bash
docker-compose up -d
```

Oracle queda en `localhost:1521` (usuario `SYSTEM`, contraseña en `docker-compose.yml`).

### Aplicación

En `application.properties` están la URL de Oracle, usuario, contraseña y `jwt.secret` / `jwt.expiration-ms`.

```bash
mvn spring-boot:run
```

Servidor: `http://localhost:8080`.

---

## Seguridad

- **JWT**: Login/register devuelven un token; el resto de la API exige `Authorization: Bearer <token>`.
- **Rutas públicas**: `/api/v1/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`.
- **Rutas protegidas**: todo lo demás bajo `/api/v1/**` requiere token válido.
- **Contraseñas**: BCrypt.
- **CORS**: configurado en `SecurityConfig` (orígenes/métodos/cabeceras según necesidad).

---

## Documentación Swagger

Con la aplicación en marcha:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

En Swagger puedes probar todos los endpoints y usar el botón "Authorize" para indicar el token JWT.

---

## Autor

Jose Maria Castro Ortega.
