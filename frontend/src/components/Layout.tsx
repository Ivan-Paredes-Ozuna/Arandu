import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth'

export function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink to="/" className="brand">
          Arandú
        </NavLink>
        <nav className="nav">
          <NavLink to="/">Catálogo</NavLink>
          {user && <NavLink to="/progreso">Mi progreso</NavLink>}
          {user && <NavLink to="/organizar">Organizar semana</NavLink>}
          {user && <NavLink to="/ayuda">Ayuda rápida</NavLink>}
        </nav>
        <div className="topbar-actions">
          {user ? (
            <>
              <span className="user-chip">{user.fullName}</span>
              <button type="button" className="btn ghost" onClick={logout}>
                Salir
              </button>
            </>
          ) : (
            <>
              <NavLink className="btn ghost" to="/login">
                Ingresar
              </NavLink>
              <NavLink className="btn primary" to="/register">
                Crear cuenta
              </NavLink>
            </>
          )}
        </div>
      </header>
      <main className="page">
        <Outlet />
      </main>
    </div>
  )
}
