import { type FormEvent, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, type Availability } from '../api'

const days: { key: keyof Omit<Availability, 'totalMinutes'>; label: string }[] = [
  { key: 'mondayMinutes', label: 'Lunes' },
  { key: 'tuesdayMinutes', label: 'Martes' },
  { key: 'wednesdayMinutes', label: 'Miércoles' },
  { key: 'thursdayMinutes', label: 'Jueves' },
  { key: 'fridayMinutes', label: 'Viernes' },
  { key: 'saturdayMinutes', label: 'Sábado' },
  { key: 'sundayMinutes', label: 'Domingo' },
]

export function OrganizePage() {
  const navigate = useNavigate()
  const [form, setForm] = useState<Omit<Availability, 'totalMinutes'>>({
    mondayMinutes: 15,
    tuesdayMinutes: 0,
    wednesdayMinutes: 30,
    thursdayMinutes: 0,
    fridayMinutes: 15,
    saturdayMinutes: 15,
    sundayMinutes: 0,
  })
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    api<Availability>('/api/availability').then((data) => {
      setForm({
        mondayMinutes: data.mondayMinutes,
        tuesdayMinutes: data.tuesdayMinutes,
        wednesdayMinutes: data.wednesdayMinutes,
        thursdayMinutes: data.thursdayMinutes,
        fridayMinutes: data.fridayMinutes,
        saturdayMinutes: data.saturdayMinutes,
        sundayMinutes: data.sundayMinutes,
      })
    })
  }, [])

  const total = Object.values(form).reduce((sum, value) => sum + Number(value || 0), 0)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setSaving(true)
    try {
      await api('/api/availability', {
        method: 'PUT',
        body: JSON.stringify(form),
      })
      await api('/api/plans/generate', { method: 'POST' })
      navigate('/plan')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section>
      <p className="eyebrow">Flexibilidad</p>
      <h1>Organizar mi semana</h1>
      <p className="lede">
        Declará pequeños espacios disponibles. El sistema responde con un plan de acciones concretas.
      </p>

      <form className="week-form" onSubmit={onSubmit}>
        {days.map((day) => (
          <label key={day.key}>
            {day.label}
            <div className="minute-input">
              <input
                type="number"
                min={0}
                max={240}
                step={5}
                value={form[day.key]}
                onChange={(e) => setForm({ ...form, [day.key]: Number(e.target.value) })}
              />
              <span>min</span>
            </div>
          </label>
        ))}
        <div className="week-total">
          <strong>{total} min disponibles</strong>
          <span>Se traducirán en bloques de 10, 15 y 30 minutos.</span>
        </div>
        <button className="btn primary" type="submit" disabled={saving || total <= 0}>
          {saving ? 'Adaptando…' : 'Adaptar mi plan'}
        </button>
      </form>
    </section>
  )
}
