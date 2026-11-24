import { type FormEvent, useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api, type Enrollment, type HelpSearch } from '../api'

export function HelpPage() {
  const [params] = useSearchParams()
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [courseId, setCourseId] = useState<number | null>(null)
  const [activityId] = useState<number | null>(
    params.get('activityId') ? Number(params.get('activityId')) : null,
  )
  const [query, setQuery] = useState(params.get('q') || 'JOIN')
  const [help, setHelp] = useState<HelpSearch | null>(null)
  const [question, setQuestion] = useState('')
  const [privateQuestion, setPrivateQuestion] = useState(true)
  const [sent, setSent] = useState(false)

  useEffect(() => {
    api<Enrollment[]>('/api/enrollments/mine').then((items) => {
      setEnrollments(items)
      const fromParam = params.get('courseId')
      if (fromParam) setCourseId(Number(fromParam))
      else if (items[0]) setCourseId(items[0].courseId)
    })
  }, [params])

  useEffect(() => {
    if (!courseId) return
    api<HelpSearch>(`/api/help?courseId=${courseId}&q=${encodeURIComponent(query)}`).then(setHelp)
  }, [courseId, query])

  async function askTutor(e: FormEvent) {
    e.preventDefault()
    if (!courseId || !question.trim()) return
    await api('/api/tutor-questions', {
      method: 'POST',
      body: JSON.stringify({
        courseId,
        activityId,
        question,
        privateQuestion,
      }),
    })
    setSent(true)
    setQuestion('')
  }

  return (
    <section className="help-layout">
      <div>
        <p className="eyebrow">Ayuda contextual</p>
        <h1>Ayuda rápida</h1>
        <p className="lede">Resolver una duda no debería romper el aprendizaje. Quedate dentro de Arandú.</p>

        <label>
          Curso
          <select value={courseId ?? ''} onChange={(e) => setCourseId(Number(e.target.value))}>
            {enrollments.map((item) => (
              <option key={item.id} value={item.courseId}>
                {item.courseTitle}
              </option>
            ))}
          </select>
        </label>

        <label>
          Buscar explicación
          <input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Ej. JOIN, claves, ritmo" />
        </label>

        {help && <p className="hint">{help.contextualHint}</p>}

        <div className="doubt-list">
          <h2>Dudas similares</h2>
          {help?.similarDoubts.map((doubt) => (
            <article key={doubt.id}>
              <h3>{doubt.question}</h3>
              <p>{doubt.answer}</p>
            </article>
          ))}
        </div>
      </div>

      <aside className="tutor-panel">
        <h2>Preguntar al tutor</h2>
        <p>Consultá con privacidad si preferís no exponerte en un foro público.</p>
        <form className="stack-form" onSubmit={askTutor}>
          <label>
            Tu consulta
            <textarea
              rows={5}
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              placeholder="Ej. No entiendo cuándo usar LEFT JOIN en la práctica de la unidad 4"
              required
            />
          </label>
          <label className="checkbox">
            <input
              type="checkbox"
              checked={privateQuestion}
              onChange={(e) => setPrivateQuestion(e.target.checked)}
            />
            <span>Enviar como consulta privada</span>
          </label>
          <button className="btn primary" type="submit">
            Enviar al tutor
          </button>
          {sent && <p className="success">Consulta enviada. Estado: pendiente.</p>}
        </form>
        <Link className="btn ghost" to="/progreso">
          Volver a Mi progreso
        </Link>
        {params.get('enrollmentId') && params.get('activityId') && (
          <Link
            className="btn ghost"
            to={`/cursado/${params.get('enrollmentId')}/${params.get('activityId')}`}
          >
            Volver a la actividad
          </Link>
        )}
      </aside>
    </section>
  )
}
