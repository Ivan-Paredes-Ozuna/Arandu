import { Navigate, Route, Routes } from 'react-router-dom'
import { Layout } from './components/Layout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { CatalogPage } from './pages/CatalogPage'
import { ConfirmationPage } from './pages/ConfirmationPage'
import { CourseDetailPage } from './pages/CourseDetailPage'
import { EnrollPage } from './pages/EnrollPage'
import { HelpPage } from './pages/HelpPage'
import { LoginPage } from './pages/LoginPage'
import { OrganizePage } from './pages/OrganizePage'
import { PlanPage } from './pages/PlanPage'
import { PlayerPage } from './pages/PlayerPage'
import { ProgressPage } from './pages/ProgressPage'
import { RegisterPage } from './pages/RegisterPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<CatalogPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="cursos/:id" element={<CourseDetailPage />} />
        <Route
          path="cursos/:id/inscripcion"
          element={
            <ProtectedRoute>
              <EnrollPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="inscripcion/ok/:enrollmentId"
          element={
            <ProtectedRoute>
              <ConfirmationPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="progreso"
          element={
            <ProtectedRoute>
              <ProgressPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="organizar"
          element={
            <ProtectedRoute>
              <OrganizePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="plan"
          element={
            <ProtectedRoute>
              <PlanPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="ayuda"
          element={
            <ProtectedRoute>
              <HelpPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="cursado/:enrollmentId/:activityId"
          element={
            <ProtectedRoute>
              <PlayerPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  )
}
