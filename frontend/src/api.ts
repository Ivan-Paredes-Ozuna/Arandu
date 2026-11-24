const TOKEN_KEY = 'arandu_token'

/** Base del backend. En local vacío → Vite proxy `/api` → :8080. En Vercel: URL pública del API. */
const API_BASE = (import.meta.env.VITE_API_URL as string | undefined)?.replace(/\/$/, '') || ''

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

export class ApiError extends Error {
  fields?: Record<string, string>
  status: number

  constructor(message: string, status: number, fields?: Record<string, string>) {
    super(message)
    this.status = status
    this.fields = fields
  }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (!headers.has('Content-Type') && options.body) {
    headers.set('Content-Type', 'application/json')
  }
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (response.status === 204) return undefined as T

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new ApiError(data.message || 'Ocurrió un error', response.status, data.fields)
  }
  return data as T
}

export type User = { id: number; fullName: string; email: string }
export type AuthResponse = { token: string; user: User }

export type CourseSummary = {
  id: number
  title: string
  description: string
  level: string
  durationHours: number
  modality: string
  rating: number
  unitCount: number
  category: string
}

export type Activity = {
  id: number
  unitNumber: number
  title: string
  type: string
  durationMinutes: number
  orderIndex: number
  summary: string
}

export type CourseDetail = CourseSummary & { activities: Activity[] }

export type Enrollment = {
  id: number
  courseId: number
  courseTitle: string
  status: string
  progressPercent: number
  nextActivityTitle: string | null
  nextActivityDurationMinutes: number | null
  nextActivityId: number | null
  daysInactive: number
  lastActivityTitle: string | null
  lastActivityUnit: number | null
}

export type ActivitySession = {
  enrollmentId: number
  activityId: number
  courseId: number
  courseTitle: string
  unitNumber: number
  title: string
  type: string
  durationMinutes: number
  percentWatched: number
  minutesRemaining: number
  summary: string
  firstAccess: boolean
  syllabus: Activity[]
}

export type Availability = {
  mondayMinutes: number
  tuesdayMinutes: number
  wednesdayMinutes: number
  thursdayMinutes: number
  fridayMinutes: number
  saturdayMinutes: number
  sundayMinutes: number
  totalMinutes: number
}

export type PlanItem = {
  id: number
  dayLabel: string
  activityId: number
  enrollmentId: number | null
  activityTitle: string
  unitNumber: number
  type: string
  durationMinutes: number
}

export type Plan = { totalMinutes: number; items: PlanItem[] }

export type HelpSearch = {
  similarDoubts: { id: number; question: string; answer: string; tags: string }[]
  contextualHint: string
}
