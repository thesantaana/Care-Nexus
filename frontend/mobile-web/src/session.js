import { reactive } from 'vue'
import { getCurrentUser, login as loginRequest, logout as logoutRequest } from './api/auth.js'
import { TOKEN_STORAGE_KEY, USER_STORAGE_KEY } from './api/request.js'

function readStoredUser() {
  try {
    const value = localStorage.getItem(USER_STORAGE_KEY)
    return value ? JSON.parse(value) : null
  } catch {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

function userFromLogin(data) {
  return {
    userId: data.userId,
    username: data.username,
    displayName: data.displayName,
    mainRoleCode: data.mainRoleCode,
    mainRoleName: data.mainRoleName,
    permissionCodes: data.permissionCodes || [],
    accountStatus: 'NORMAL'
  }
}

export const sessionState = reactive({
  token: localStorage.getItem(TOKEN_STORAGE_KEY),
  user: readStoredUser(),
  verified: false,
  checking: false,
  error: ''
})

let restorePromise = null

function persistSession(token, user) {
  sessionState.token = token
  sessionState.user = user
  localStorage.setItem(TOKEN_STORAGE_KEY, token)
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user))
}

export function clearSession() {
  sessionState.token = null
  sessionState.user = null
  sessionState.verified = false
  sessionState.checking = false
  sessionState.error = ''
  localStorage.removeItem(TOKEN_STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
  restorePromise = null
}

export async function signIn(credentials) {
  sessionState.checking = true
  sessionState.error = ''
  try {
    const loginData = await loginRequest(credentials)
    if (!loginData?.token) {
      throw new Error('登录响应缺少 Token，请联系管理员。')
    }

    persistSession(loginData.token, userFromLogin(loginData))
    const currentUser = await getCurrentUser()
    persistSession(loginData.token, currentUser)
    sessionState.verified = true
    return currentUser
  } catch (error) {
    clearSession()
    throw error
  } finally {
    sessionState.checking = false
  }
}

export async function ensureSession({ force = false } = {}) {
  if (!sessionState.token) {
    return null
  }
  if (sessionState.verified && sessionState.user && !force) {
    return sessionState.user
  }
  if (restorePromise && !force) {
    return restorePromise
  }

  sessionState.checking = true
  sessionState.error = ''
  restorePromise = getCurrentUser()
    .then((currentUser) => {
      persistSession(sessionState.token, currentUser)
      sessionState.verified = true
      return currentUser
    })
    .catch((error) => {
      sessionState.verified = false
      sessionState.error = error.message || '登录状态校验失败。'
      if (error.status === 401) {
        clearSession()
      }
      return null
    })
    .finally(() => {
      sessionState.checking = false
      restorePromise = null
    })

  return restorePromise
}

export async function refreshCurrentUser() {
  return ensureSession({ force: true })
}

export async function signOut() {
  let remoteError = null
  try {
    if (sessionState.token) {
      await logoutRequest()
    }
  } catch (error) {
    if (error.status !== 401) {
      remoteError = error
    }
  } finally {
    clearSession()
  }

  if (remoteError) {
    throw remoteError
  }
}

export function hasPermission(permissionCode) {
  return Boolean(sessionState.user?.permissionCodes?.includes(permissionCode))
}
