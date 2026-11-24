import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api, type CourseDetail } from '../api'
import { useAuth } from '../auth'

export function CourseDetailPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [course, setCourse] = useState<CourseDetail | null>(null)

  useEffect(() => {
    if (!id) return
    api<CourseDetail>(`/api/courses/${id}`).then(setCourse)
  }, [id])

  if (!course) return <p className="muted">Cargando detalle…</p>

  return (
    <section className="detail-layout">
      <div>
        <p className="eyebrow">{course.category}</p>
        <h1>{course.title}</h1>
        <p className="lede">{course.description}</p>
        <div className="fact-grid">
          <div>
            <strong>Nivel</strong>
            <span>{course.level}</span>
          </div>
          <div>
            <strong>Duración</strong>
            <span>{course.durationHours} horas</span>
          </div>
          <div>
            <strong>Unidades</strong>
            <span>{course.unitCount}</span>
          </div>
          <div>
            <strong>Modalidad</strong>
            <span>{course.modality}</span>
          </div>
          <div>
            <strong>Valoración</strong>
            <span>{course.rating.toFixed(1)} ★</span>
          </div>
        </div>
        <div className="cta-row">
          <button
            className="btn primary"
            type="button"
            onClick={() => {
              if (!user) navigate('/login', { state: { from: `/cursos/${course.id}/inscripcion` } })
              else navigate(`/cursos/${course.id}/inscripcion`)
            }}
          >
            Me inscribo
          </button>
          <Link className="btn ghost" to="/">
            Volver al catálogo
          </Link>
        </div>
        <p className="hint">Inscripción sin pago. El plan se adapta a tu tiempo disponible.</p>
      </div>
      <aside className="syllabus">
        <h2>Unidades</h2>
        <ol>
          {course.activities.map((activity) => (
            <li key={activity.id}>
              <strong>
                U{activity.unitNumber} · {activity.title}
              </strong>
              <span>
                {activity.type} · {activity.durationMinutes} min
              </span>
            </li>
          ))}
        </ol>
      </aside>
    </section>
  )
}
