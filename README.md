# Arandú

Plataforma de cursos orientada a **sostener el aprendizaje** cuando el estudio compite con la facultad, el trabajo y la vida diaria.

Stack: **React (Vite + TypeScript)** + **Spring Boot 3** + **H2** + **JWT**.

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

## Arranque

### Backend

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk/25.0.2/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
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
