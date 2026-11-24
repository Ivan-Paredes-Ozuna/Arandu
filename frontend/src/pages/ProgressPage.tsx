import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type Enrollment } from '../api'

export function ProgressPage() {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api<Enrollment[]>('/api/enrollments/mine')
      .then(setEnrollments)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <p className="muted">Cargando progreso…</p>

  return (
    <section>
      <p className="eyebrow">Seguimiento</p>
      <h1>Mi progreso</h1>
      <p className="lede">Retomar debería requerir una sola decisión. Acá ves avance, inactividad y el siguiente paso.</p>

      {!enrollments.length && (
        <div className="empty-state">
          <p>Todavía no estás inscripto en un curso.</p>
          <Link className="btn primary" to="/">
            Ir al catálogo
          </Link>
        </div>
      )}

      <div className="progress-list">
        {enrollments.map((item) => (
          <article key={item.id} className="progress-card">
            <div className="progress-head">
              <h2>{item.courseTitle}</h2>
              <span className="badge">{item.status}</span>
            </div>
            <div className="progress-meter" aria-label={`${item.progressPercent}% de avance`}>
              <div style={{ width: `${item.progressPercent}%` }} />
            </div>
            <p className="big-number">{item.progressPercent}% de avance</p>
            <ul className="plain-list">
              <li>
                Última actividad: {item.lastActivityTitle || '—'}
                {item.lastActivityUnit ? ` (Unidad ${item.lastActivityUnit})` : ''}
              </li>
              <li>
                {item.daysInactive > 0
                  ? `${item.daysInactive} día${item.daysInactive === 1 ? '' : 's'} sin continuar`
                  : 'Activo hoy'}
              </li>
              <li>
                Próxima: {item.nextActivityTitle} · {item.nextActivityDurationMinutes} min
              </li>
            </ul>
            <div className="cta-row">
              <Link className="btn primary" to={`/cursado/${item.id}/${item.nextActivityId}`}>
                Retomar ahora
              </Link>
              <Link className="btn ghost" to="/organizar">
                Organizar mi tiempo
              </Link>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
