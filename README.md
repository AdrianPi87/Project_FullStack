# Product Configurator Demo

Demo full-stack de una aplicacion de seleccion, configuracion y simulacion de productos. Incluye catalogo, configurador con opciones, validacion de compatibilidad, carrito, checkout, ordenes, autenticacion JWT y eventos de analitica.

## Stack tecnologico

- Backend: Java 17, Spring Boot 4.1, Spring MVC, Spring Data JPA, Spring Security, H2 en memoria, Maven.
- Frontend: React 19, TypeScript, Redux Toolkit (RTK Query), React Router 7, Vite 8, Tailwind CSS 4.
- Pruebas: JUnit 5 + MockMvc en backend, Vitest + React Testing Library en frontend.
- Documentacion API: Springdoc OpenAPI / Swagger UI.
- Observabilidad: Spring Boot Actuator + Micrometer Prometheus.

## Estructura del proyecto

```text
Project_Fullstack/
|-- README.md
|-- docker-compose.yml
|-- backend/
|   |-- Dockerfile
|   |-- .dockerignore
|   |-- pom.xml
|   |-- mvnw / mvnw.cmd
|   `-- src/
|       |-- main/java/com/demo/configurator/
|       |   |-- controller/     Controladores REST
|       |   |-- service/        Logica de negocio
|       |   |-- model/          Entidades JPA
|       |   |-- repository/     Repositorios Spring Data
|       |   |-- security/       JWT y seguridad
|       |   |-- config/         CORS, seeder, filtros
|       |   |-- dto/            Contratos request/response
|       |   `-- exception/      Manejo de errores
|       |-- main/resources/application.properties
|       `-- test/               Pruebas backend
`-- frontend/
    |-- Dockerfile
    |-- nginx.conf
    |-- .dockerignore
    |-- package.json
    |-- index.html
    `-- src/
        |-- api/productApi.ts    Cliente API con RTK Query
        |-- app/                 Store y hooks Redux
        |-- pages/               Pantallas principales
        |-- types.ts             Tipos TypeScript
        `-- utils/session.ts     Gestion de sessionId
```

## Funcionalidades principales

- Catalogo con busqueda por nombre/descripcion, filtro por categoria y orden (nombre o precio).
- Detalle de producto con grupos de opcion.
- Configuracion de opciones single/multi con limites de seleccion.
- Validacion de opciones requeridas, stock y compatibilidad (EXCLUDES / REQUIRES).
- Calculo de precio base + deltas de opciones.
- Carrito con alta, actualizacion de cantidad, eliminacion y merge.
- Checkout con validacion de stock y descuento de inventario.
- Historial de ordenes por usuario.
- Registro e inicio de sesion con JWT.
- Migracion del carrito anonimo al carrito del usuario al autenticarse.
- Eventos de analitica (product_viewed, add_to_cart, checkout_completed).
- Consola H2, Swagger UI, Prometheus y health/info.

## Requisitos para desarrollo local

- JDK 17 o superior.
- Maven Wrapper incluido; requiere internet en la primera compilacion.
- Node.js 18 o superior.
- npm.

## Ejecucion local: backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

- Backend URL: http://localhost:8080
- H2 console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:configdb
- Usuario H2: sa
- Password H2: vacio

## Ejecucion local: frontend

```powershell
cd frontend
npm install
npm run dev
```

- Frontend URL: http://localhost:5173
- Vite hace proxy de `/api` hacia `http://localhost:8080`.

## Ejecucion con Docker

Levanta backend y frontend en un solo comando:

```powershell
docker compose up --build
```

- Frontend: http://localhost
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- Variables usadas en compose: `APP_JWT_SECRET`.

## Credenciales demo

- Admin:
  - Email: admin@example.com
  - Password: Admin123!
- Puedes crear usuarios normales desde `/register`.

Los datos iniciales se cargan en `DataSeeder`. El admin se recrea si no existe. Si la base ya tiene productos, no se reseedean productos para evitar duplicados.

## Variables de configuracion

Backend:

- `APP_JWT_SECRET`: secreto JWT. Por defecto se usa un valor demo.
- `server.port`: puerto del backend (8080).
- `spring.datasource.url`: conexion a H2 en memoria.

Frontend:

- `VITE` proxies `/api` por defecto a `:8080` en `vite.config.ts`.
- El token, role y sessionId se guardan en localStorage para la demo.

## Flujo principal de usuario

1. El usuario explora el catalogo y filtra/busca productos.
2. Abre un producto y selecciona opciones.
3. El frontend envia configuracion para validar y calcular precio.
4. Al marcar "Validate & Add to Cart", se valida y agrega al carrito.
5. En `/cart` actualiza cantidades o elimina items.
6. Checkout crea la orden y descuenta stock.
7. El usuario ve el historial en `/orders`.

## Manejo de sesion y autenticacion

- Usuarios anonimos usan un sessionId generado en localStorage.
- Usuarios autenticados usan su id de usuario como sessionId.
- Al login/register se migra el carrito anonimo al carrito del usuario mediante `/api/cart/merge`.
- Las rutas protegidas del backend requieren `Authorization: Bearer <token>`.
- Endpoints publicos:
  - `/api/auth/**`
  - `GET /api/products/**`
  - `POST /api/configurations/**`
  - documentacion, health, info y prometheus.

## Reglas de compatibilidad demo

- Integrated Graphics excluye 16" QHD+ display.
- 16" QHD+ display requiere NVIDIA RTX 4060 o RTX 4070 (implementado como exclusion con Integrated Graphics).

## API principal

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/api/products` | Lista productos activos |
| GET | `/api/products/{id}` | Detalle de producto con grupos de opcion |
| POST | `/api/configurations` | Valida y calcula precio de configuracion |
| GET | `/api/configurations/{id}` | Obtiene configuracion persistida |
| GET | `/api/cart?sessionId=` | Obtiene carrito |
| POST | `/api/cart/items` | Agrega item configurado al carrito |
| POST | `/api/cart/merge` | Fusiona carrito anonimo con carrito de usuario |
| PATCH | `/api/cart/items/{id}?sessionId=` | Actualiza cantidad |
| DELETE | `/api/cart/items/{id}?sessionId=` | Elimina item |
| POST | `/api/orders` | Checkout del carrito |
| GET | `/api/orders?sessionId=` | Lista ordenes |
| POST | `/api/analytics/events` | Registra evento de analitica |
| GET | `/api/analytics/events` | Lista ultimos eventos |
| POST | `/api/auth/register` | Crea cuenta |
| POST | `/api/auth/login` | Inicia sesion |
| GET | `/api/admin/users` | Lista usuarios (solo ADMIN) |

## Manejo de errores

El backend devuelve `ApiErrorResponse` con campos:

- timestamp
- status
- error
- message
- path
- traceId

El frontend muestra `message` cuando esta disponible para dar feedback util al usuario.

## Pruebas

Backend:

```powershell
cd backend
.\mvnw.cmd test
```

Cubre:

- Carga de contexto Spring Boot.
- Listado de productos sembrados.
- Detalle con grupos de opcion.
- Configuracion valida.
- Rechazo de seleccion incompatible.

Frontend:

```powershell
cd frontend
npm test
```

Cubre:

- Render de pagina de productos con mock de API.

Build y lint frontend:

```powershell
cd frontend
npm run build
npm run lint
```

## Documentacion y observabilidad

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health
- Info: http://localhost:8080/actuator/info
- Prometheus: http://localhost:8080/actuator/prometheus
- H2 console: http://localhost:8080/h2-console

## Notas de produccion

- H2 es en memoria y solo sirve para demo. Reemplazar por PostgreSQL o MySQL con Flyway para produccion.
- Configurar un secreto JWT fuerte mediante `APP_JWT_SECRET`.
- Ajustar origenes CORS en `CorsConfig` para dominios reales.
- Considerar reservas de stock si se requiere concurrencia alta.
- Asociar ordenes a usuarios autenticados (ya usa resolver por id en cart/orders).

## Mejoras sugeridas

- Tests de auth, carrito, checkout y configurador.
- Panel admin para gestionar productos/opciones/stock.
- E2E con Playwright.
- CI pipeline.
- Filtros y busqueda avanzada en backend.
- Migraciones de base de datos con Flyway.