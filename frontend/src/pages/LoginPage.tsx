import { type FormEvent, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { ApiError } from '../api'
import { useAuth } from '../auth'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('ana@arandu.edu.py')
  const [password, setPassword] = useState('arandu123')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await login(email, password)
      const from = (location.state as { from?: string } | null)?.from || '/progreso'
      navigate(from)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo iniciar sesión')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="auth-panel">
      <p className="eyebrow">Continuar donde quedaste</p>
      <h1>Ingresar a Arandú</h1>
      <p className="lede">Usá tu cuenta para retomar el curso, adaptar tu semana y pedir ayuda sin salir de la plataforma.</p>
      <form className="stack-form" onSubmit={onSubmit}>
        <label>
          Correo electrónico
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </label>
        <label>
          Contraseña
          <input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
        </label>
        {error && <p className="field-error">{error}</p>}
        <button className="btn primary" disabled={submitting} type="submit">
          {submitting ? 'Ingresando…' : 'Ingresar'}
        </button>
      </form>
      <p className="muted">
        ¿No tenés cuenta? <Link to="/register">Crear cuenta</Link>
      </p>
      <p className="hint">Demo: ana@arandu.edu.py / arandu123 (curso Bases de Datos al 42%)</p>
    </section>
  )
}
