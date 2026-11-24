import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, type ActivitySession } from '../api'

export function PlayerPage() {
  const { enrollmentId, activityId } = useParams()
  const [session, setSession] = useState<ActivitySession | null>(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!enrollmentId || !activityId) return
    api<ActivitySession>(`/api/enrollments/${enrollmentId}/activities/${activityId}`).then(setSession)
  }, [enrollmentId, activityId])

  async function updatePercent(percent: number) {
    if (!enrollmentId || !activityId) return
    setSaving(true)
    try {
      const updated = await api<ActivitySession>(
        `/api/enrollments/${enrollmentId}/activities/${activityId}/progress`,
        {
          method: 'PATCH',
          body: JSON.stringify({ percentWatched: percent }),
        },
      )
      setSession(updated)
    } finally {
      setSaving(false)
    }
  }

  if (!session) return <p className="muted">Cargando cursado…</p>

  return (
    <section className="player-layout">
      <div>
        <p className="eyebrow">
          {session.firstAccess ? 'Inicio del cursado' : 'Reanudación'} · Unidad {session.unitNumber}
        </p>
        <h1>{session.title}</h1>
        <p className="lede">{session.summary}</p>

        <div className="player-stage" aria-label="Reproductor simulado">
          <div className="player-copy">
            <strong>{session.courseTitle}</strong>
            <span>
              {session.percentWatched}% de actividad vista · ≈ {session.minutesRemaining} min por completar
            </span>
          </div>
          <div className="progress-meter tall">
            <div style={{ width: `${session.percentWatched}%` }} />
          </div>
          <div className="player-controls">
            <button className="btn ghost" type="button" disabled={saving} onClick={() => updatePercent(Math.max(0, session.percentWatched - 10))}>
              -10%
            </button>
            <button className="btn primary" type="button" disabled={saving} onClick={() => updatePercent(Math.min(100, session.percentWatched + 15))}>
              Avanzar 15%
            </button>
            <button className="btn ghost" type="button" disabled={saving} onClick={() => updatePercent(100)}>
              Marcar completa
            </button>
          </div>
        </div>

        <div className="cta-row">
          <Link className="btn ghost" to="/progreso">
            Volver a Mi progreso
          </Link>
          <Link
            className="btn primary"
            to={`/ayuda?courseId=${session.courseId}&enrollmentId=${session.enrollmentId}&activityId=${session.activityId}&q=${encodeURIComponent(session.title)}`}
          >
            Pedir ayuda
          </Link>
        </div>
      </div>

      <aside className="syllabus">
        <h2>Contenidos</h2>
        <ol>
          {session.syllabus.map((item) => (
            <li key={item.id} className={item.id === session.activityId ? 'current' : ''}>
              <Link to={`/cursado/${session.enrollmentId}/${item.id}`}>
                U{item.unitNumber} · {item.title}
              </Link>
              <span>
                {item.type} · {item.durationMinutes} min
              </span>
            </li>
          ))}
        </ol>
      </aside>
    </section>
  )
}
