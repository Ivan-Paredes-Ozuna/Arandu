import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../api'
import { useAuth } from '../auth'

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fields, setFields] = useState<Record<string, string>>({})
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    setFields({})
    try {
      await register(fullName, email, password)
      navigate('/')
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
        setFields(err.fields || {})
      } else {
        setError('No se pudo crear la cuenta')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="auth-panel">
      <p className="eyebrow">Empezar en Arandú</p>
      <h1>Crear cuenta</h1>
      <p className="lede">Inscribite a cursos sin pago y sostené tu ritmo con bloques cortos.</p>
      <form className="stack-form" onSubmit={onSubmit}>
        <label className={fields.fullName ? 'has-error' : ''}>
          Nombre completo
          <input value={fullName} onChange={(e) => setFullName(e.target.value)} required />
          {fields.fullName && <span className="field-error">{fields.fullName}</span>}
        </label>
        <label className={fields.email ? 'has-error' : ''}>
          Correo electrónico
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
          {fields.email && <span className="field-error">{fields.email}</span>}
        </label>
        <label className={fields.password ? 'has-error' : ''}>
          Contraseña
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required minLength={6} />
          {fields.password && <span className="field-error">{fields.password}</span>}
        </label>
        {error && !Object.keys(fields).length && <p className="field-error">{error}</p>}
        <button className="btn primary" disabled={submitting} type="submit">
          {submitting ? 'Creando…' : 'Crear cuenta'}
        </button>
      </form>
      <p className="muted">
        ¿Ya tenés cuenta? <Link to="/login">Ingresar</Link>
      </p>
    </section>
  )
}
