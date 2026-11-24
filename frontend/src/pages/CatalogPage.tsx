import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type CourseSummary } from '../api'

export function CatalogPage() {
  const [courses, setCourses] = useState<CourseSummary[]>([])
  const [level, setLevel] = useState('')
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api<CourseSummary[]>('/api/courses')
      .then(setCourses)
      .finally(() => setLoading(false))
  }, [])

  const categories = useMemo(
    () => Array.from(new Set(courses.map((c) => c.category))).sort(),
    [courses],
  )

  const filtered = useMemo(
    () =>
      courses.filter(
        (course) =>
          (!level || course.level === level) && (!category || course.category === category),
      ),
    [courses, level, category],
  )

  return (
    <section className="catalog">
      <div className="hero-copy">
        <p className="eyebrow">Explorar</p>
        <h1>Encontrá un curso. Diseñamos cómo seguir.</h1>
        <p className="lede">
          Tarjetas equivalentes, filtros visibles y una sola acción clara: Ver detalles.
        </p>
      </div>

      <div className="filters">
        <label>
          Nivel
          <select value={level} onChange={(e) => setLevel(e.target.value)}>
            <option value="">Todos</option>
            <option value="Principiante">Principiante</option>
            <option value="Intermedio">Intermedio</option>
          </select>
        </label>
        <label>
          Categoría
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            <option value="">Todas</option>
            {categories.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </label>
      </div>

      {loading ? (
        <p className="muted">Cargando cursos…</p>
      ) : (
        <div className="course-grid">
          {filtered.map((course) => (
            <article key={course.id} className="course-card">
              <div className="meta-row">
                <span>{course.level}</span>
                <span>{course.durationHours} h</span>
                <span>{course.rating.toFixed(1)} ★</span>
              </div>
              <h2>{course.title}</h2>
              <p>{course.description}</p>
              <p className="muted">
                {course.unitCount} unidades · {course.modality}
              </p>
              <Link className="btn primary" to={`/cursos/${course.id}`}>
                Ver detalles
              </Link>
            </article>
          ))}
        </div>
      )}
    </section>
  )
}
