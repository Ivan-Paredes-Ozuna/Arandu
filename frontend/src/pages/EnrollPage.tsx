import { type FormEvent, useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api, ApiError, type CourseDetail, type Enrollment } from '../api'
import { useAuth } from '../auth'

export function EnrollPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [course, setCourse] = useState<CourseDetail | null>(null)
  const [fullName, setFullName] = useState(user?.fullName || '')
  const [email, setEmail] = useState(user?.email || '')
  const [acceptTerms, setAcceptTerms] = useState(false)
  const [fields, setFields] = useState<Record<string, string>>({})
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (!id) return
    api<CourseDetail>(`/api/courses/${id}`).then(setCourse)
  }, [id])

  useEffect(() => {
    if (user) {
      setFullName(user.fullName)
      setEmail(user.email)
    }
  }, [user])

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    if (!course) return
    setSubmitting(true)
    setError(null)
    setFields({})
    try {
      const enrollment = await api<Enrollment>('/api/enrollments', {
        method: 'POST',
        body: JSON.stringify({
          courseId: course.id,
          fullName,
          email,
          acceptTerms,
        }),
      })
      navigate(`/inscripcion/ok/${enrollment.id}`)
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
        setFields(err.fields || {})
      } else {
        setError('No se pudo completar la inscripción')
      }
    } finally {
      setSubmitting(false)
    }
  }

  if (!course) return <p className="muted">Cargando inscripción…</p>

  const hasFieldErrors = Object.keys(fields).length > 0

  return (
    <section className="enroll-layout">
      <div>
        <p className="eyebrow">Inscripción sin pago</p>
        <h1>Confirmar datos</h1>
        <p className="lede">Pedimos solamente lo necesario. Si hay un error, lo señalamos sin reiniciar todo.</p>

        <form className="stack-form" onSubmit={onSubmit} noValidate>
          <label className={fields.fullName ? 'has-error' : ''}>
            Nombre completo *
            <input value={fullName} onChange={(e) => setFullName(e.target.value)} />
            <small>Como querés que figure en tu progreso.</small>
            {fields.fullName && <span className="field-error">{fields.fullName}</span>}
          </label>

          <label className={fields.email ? 'has-error' : ''}>
            Correo electrónico *
            <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" />
            <small>Usamos este correo para avisos de reenganche.</small>
            {fields.email && <span className="field-error">{fields.email}</span>}
          </label>

          <label className={`checkbox ${fields.acceptTerms ? 'has-error' : ''}`}>
            <input
              type="checkbox"
              checked={acceptTerms}
              onChange={(e) => setAcceptTerms(e.target.checked)}
            />
            <span>Acepto las condiciones del curso y confirmo que la inscripción no implica pago.</span>
          </label>
          {fields.acceptTerms && <span className="field-error">{fields.acceptTerms}</span>}

          {hasFieldErrors && (
            <div className="error-banner" role="alert">
              <strong>Hay datos por corregir</strong>
              <p>{fields.email || error || 'Revisá los campos marcados.'}</p>
            </div>
          )}
          {error && !hasFieldErrors && <p className="field-error">{error}</p>}

          <div className="cta-row">
            <button className="btn primary" type="submit" disabled={submitting}>
              {submitting ? 'Confirmando…' : 'Confirmar inscripción'}
            </button>
            {hasFieldErrors ? (
              <button className="btn ghost" type="button" onClick={() => setFields({})}>
                Corregir datos
              </button>
            ) : (
              <Link className="btn ghost" to={`/cursos/${course.id}`}>
                Cancelar
              </Link>
            )}
          </div>
        </form>
      </div>

      <aside className="summary-panel">
        <h2>Resumen</h2>
        <p className="summary-title">{course.title}</p>
        <ul>
          <li>Nivel: {course.level}</li>
          <li>Duración: {course.durationHours} h</li>
          <li>Unidades: {course.unitCount}</li>
          <li>Modalidad: {course.modality}</li>
          <li>Valoración: {course.rating.toFixed(1)} ★</li>
          <li>Pago: no requerido</li>
        </ul>
        <p className="hint">Próximo paso tras confirmar: Unidad 1 · {course.activities[0]?.title}</p>
      </aside>
    </section>
  )
}
