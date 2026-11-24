import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api, type Enrollment } from '../api'

export function ConfirmationPage() {
  const { enrollmentId } = useParams()
  const [enrollment, setEnrollment] = useState<Enrollment | null>(null)

  useEffect(() => {
    if (!enrollmentId) return
    api<Enrollment>(`/api/enrollments/${enrollmentId}`).then(setEnrollment)
  }, [enrollmentId])

  if (!enrollment) return <p className="muted">Cargando confirmación…</p>

  return (
    <section className="confirm-panel">
      <p className="eyebrow">Resultado</p>
      <h1>Inscripción exitosa</h1>
      <p className="lede">
        Ya estás en <strong>{enrollment.courseTitle}</strong>. El sistema te orienta al siguiente paso sin
        confundir un inicio con una reanudación.
      </p>

      <div className="status-strip">
        <div>
          <span className="label">Estado</span>
          <strong>Inscripto</strong>
        </div>
        <div>
          <span className="label">Progreso</span>
          <strong>{enrollment.progressPercent} %</strong>
        </div>
        <div>
          <span className="label">Siguiente actividad</span>
          <strong>
            {enrollment.nextActivityTitle} · {enrollment.nextActivityDurationMinutes} min
          </strong>
        </div>
      </div>

      <div className="cta-row">
        <Link
          className="btn primary"
          to={`/cursado/${enrollment.id}/${enrollment.nextActivityId}`}
        >
          Empezar a cursar
        </Link>
        <Link className="btn ghost" to="/progreso">
          Ir a Mi progreso
        </Link>
      </div>
    </section>
  )
}
