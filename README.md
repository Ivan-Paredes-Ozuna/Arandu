# Arandú

Plataforma de cursos orientada a **sostener el aprendizaje** cuando el estudio compite con la facultad, el trabajo y la vida diaria.

Stack: **React (Vite + TypeScript)** + **Spring Boot 3** + **PostgreSQL (Supabase)** + **Flyway** + **JWT**.

Basado en los entregables IHC del Grupo 1 (investigación, journey map, prototipo y evaluación heurística).

## Rutas de la experiencia

**Ruta A — Inscribirse y empezar**

Catálogo → Detalle → Inscripción (con error de correo recuperable) → Confirmación → Cursado (Unidad 1)

**Ruta B — Retomar el aprendizaje**

Mi progreso → Organizar semana → Plan adaptado → Cursado / Ayuda rápida

## Requisitos

- Java 21+ (probado con OpenJDK 25 de Homebrew)
- Maven 3.9+
- Node.js 20+
- Proyecto Supabase (PostgreSQL)

## Base de datos (Supabase)

Migraciones en dos lugares (mismo SQL):

| Uso | Ruta |
|-----|------|
| Flyway (Spring Boot al arrancar) | [`backend/src/main/resources/db/migration/`](backend/src/main/resources/db/migration/) |
| Supabase CLI / SQL Editor | [`supabase/migrations/`](supabase/migrations/) |

1. `V1` / `…_create_schema.sql` — tablas e índices  
2. `V2` / `…_seed_demo.sql` — cursos, Ana Demo (42%), JOIN pendiente  

**Opción A — que Flyway migre solo**

```bash
export DATABASE_URL='jdbc:postgresql://db.YOUR_REF.supabase.co:5432/postgres?sslmode=require'
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD='YOUR_DB_PASSWORD'
cd backend && mvn spring-boot:run
```

**Opción B — SQL Editor de Supabase**

Pegá y ejecutá en orden los archivos de [`supabase/migrations/`](supabase/migrations/).  
Si ya corriste el SQL a mano, podés desactivar Flyway: `SPRING_FLYWAY_ENABLED=false`.

Plantilla de variables: [`backend/.env.example`](backend/.env.example).

## Arranque

### Backend

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
export DATABASE_URL='jdbc:postgresql://db.YOUR_REF.supabase.co:5432/postgres?sslmode=require'
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD='YOUR_DB_PASSWORD'
cd backend
mvn spring-boot:run
```

API en `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App en `http://localhost:5173` (proxy `/api` → backend)

## Variables de entorno

### Vercel (solo frontend)

En Vercel → Project → Settings → Environment Variables:

| Variable | Valor | Notas |
|----------|--------|--------|
| `VITE_API_URL` | `https://TU-BACKEND.ejemplo.com` | URL pública del Spring Boot, **sin** `/` final |

No pongas en Vercel la DB ni el JWT: el frontend no los usa. El JWT del usuario se guarda en `localStorage` tras el login.

Plantilla: [`frontend/.env.example`](frontend/.env.example).

### Backend (Render / Railway / VPS — no Vercel)

Estas van donde corra Spring Boot ([`backend/.env.example`](backend/.env.example)):

| Variable | Origen |
|----------|--------|
| `DATABASE_URL` | Supabase → Project Settings → Database → Connection string (JDBC + `?sslmode=require`) |
| `DATABASE_USERNAME` | normalmente `postgres` |
| `DATABASE_PASSWORD` | password de la DB en Supabase |
| `JWT_SECRET` | clave larga propia (≥32 chars) para firmar tokens |

Spring Boot **no** se despliega en Vercel; el frontend en Vercel llama al API vía `VITE_API_URL`.

## Usuario demo

| Campo | Valor |
|-------|--------|
| Correo | `ana@arandu.edu.py` |
| Contraseña | `arandu123` |

Ana ya tiene **Bases de Datos** al **42%**, con 5 días de inactividad y actividad pendiente **JOIN en SQL** (Unidad 4), ideal para probar “Retomar ahora”.

## Funciones implementadas

- Registro / login JWT
- Catálogo con filtros, detalle e inscripción sin pago
- Validación de correo y estado de error con corrección
- Confirmación con estado Inscripto · 0 %
- Cursado con progreso persistido y distinción inicio / reanudación
- Mi progreso, organización semanal y plan en bloques de 10/15/30 min
- Ayuda contextual, dudas similares y consulta privada al tutor
