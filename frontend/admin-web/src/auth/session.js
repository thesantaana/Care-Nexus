import { computed, reactive } from 'vue'
import { getCurrentUser, login as loginRequest, logout as logoutRequest } from '../api/auth.js'
import { TOKEN_STORAGE_KEY, USER_STORAGE_KEY } from '../api/request.js'

function readStoredUser() {
  const stored = localStorage.getItem(USER_STORAGE_KEY)
  if (!stored) {
    return null
  }

  try {
    return JSON.parse(stored)
  } catch {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

export const session = reactive({
  token: localStorage.getItem(TOKEN_STORAGE_KEY),
  user: readStoredUser(),
  initialized: false,
  loading: false
})

export const isAuthenticated = computed(() => Boolean(session.token && session.user))

function userFromLogin(response) {
  const user = { ...response }
  delete user.token
  delete user.tokenType
  delete user.expiresIn
  delete user.expiresAt
  return user
}

function persistUser(user) {
  session.user = user
  if (user) {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
  } else {
    localStorage.removeItem(USER_STORAGE_KEY)
  }
}

export function clearSession() {
  session.token = null
  session.user = null
  localStorage.removeItem(TOKEN_STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
}

export async function signIn(credentials) {
  session.loading = true
  try {
    const loginResponse = await loginRequest(credentials)
    session.token = loginResponse.token
    localStorage.setItem(TOKEN_STORAGE_KEY, loginResponse.token)
    persistUser(userFromLogin(loginResponse))

    const currentUser = await getCurrentUser()
    persistUser(currentUser)
    return currentUser
  } catch (error) {
    clearSession()
    throw error
  } finally {
    session.loading = false
  }
}

export async function initializeSession() {
  if (session.initialized) {
    return
  }

  if (!session.token) {
    clearSession()
    session.initialized = true
    return
  }

  try {
    persistUser(await getCurrentUser())
  } catch {
    clearSession()
  } finally {
    session.initialized = true
  }
}

export async function signOut() {
  session.loading = true
  try {
    if (session.token) {
      await logoutRequest()
    }
  } finally {
    clearSession()
    session.loading = false
  }
}

export function hasPermission(permission) {
  return Boolean(session.user?.permissionCodes?.includes(permission))
}

export function hasAnyPermission(permissions = []) {
  return permissions.length === 0 || permissions.some(hasPermission)
}

export function hasRole(roles = []) {
  return roles.length === 0 || roles.includes(session.user?.mainRoleCode)
}
