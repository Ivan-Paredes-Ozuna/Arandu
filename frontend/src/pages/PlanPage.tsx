import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, type Plan } from '../api'

export function PlanPage() {
  const [plan, setPlan] = useState<Plan | null>(null)

  useEffect(() => {
    api<Plan>('/api/plans/current').then(setPlan)
  }, [])

  const grouped = useMemo(() => {
    if (!plan) return []
    const map = new Map<string, typeof plan.items>()
    for (const item of plan.items) {
      const list = map.get(item.dayLabel) || []
      list.push(item)
      map.set(item.dayLabel, list)
    }
    return Array.from(map.entries())
  }, [plan])

  if (!plan) return <p className="muted">Cargando plan…</p>

  return (
    <section>
      <p className="eyebrow">Plan adaptado</p>
      <h1>{plan.totalMinutes} min → actividades concretas</h1>
      <p className="lede">
        Ejemplo del recorrido investigado: video corto, ejercicio y práctica repartidos en tus huecos reales.
      </p>

      {!plan.items.length && (
        <div className="empty-state">
          <p>Todavía no hay un plan generado.</p>
          <Link className="btn primary" to="/organizar">
            Organizar mi semana
          </Link>
        </div>
      )}

      <div className="plan-days">
        {grouped.map(([day, items]) => (
          <article key={day} className="plan-day">
            <h2>{day}</h2>
            <ul>
              {items.map((item) => (
                <li key={item.id}>
                  <div>
                    <strong>
                      {item.durationMinutes} min · {item.activityTitle}
                    </strong>
                    <span>
                      Unidad {item.unitNumber} · {item.type}
                    </span>
                  </div>
                  {item.enrollmentId && (
                    <Link className="btn ghost" to={`/cursado/${item.enrollmentId}/${item.activityId}`}>
                      Abrir
                    </Link>
                  )}
                </li>
              ))}
            </ul>
          </article>
        ))}
      </div>

      <div className="cta-row">
        <Link className="btn ghost" to="/organizar">
          Editar tiempos
        </Link>
        <Link className="btn primary" to="/progreso">
          Volver a Mi progreso
        </Link>
      </div>
    </section>
  )
}
