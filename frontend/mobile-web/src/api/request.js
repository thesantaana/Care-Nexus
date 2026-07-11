const baseUrl = (import.meta.env.VITE_API_BASE_URL || '/api/v1').replace(/\/$/, '')

export const TOKEN_STORAGE_KEY = 'care-nexus-token'
export const USER_STORAGE_KEY = 'care-nexus-user'
export const UNAUTHORIZED_EVENT = 'care-nexus:unauthorized'

export class ApiError extends Error {
  constructor(message, { status = 0, code = 'NETWORK_ERROR', data = null } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.data = data
  }
}

function clearStoredSession() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
}

function buildUrl(path, query) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const url = new URL(`${baseUrl}${normalizedPath}`)

  Object.entries(query || {}).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, String(value))
    }
  })

  return url.toString()
}

export async function request(path, options = {}) {
  const {
    query,
    auth = true,
    emitUnauthorized = true,
    json,
    headers: providedHeaders,
    ...fetchOptions
  } = options
  const headers = new Headers(providedHeaders || {})
  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  let body = fetchOptions.body

  if (auth && token) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  if (json !== undefined) {
    headers.set('Content-Type', 'application/json')
    body = JSON.stringify(json)
  } else if (body && !(body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  let response
  try {
    response = await fetch(buildUrl(path, query), {
      ...fetchOptions,
      body,
      headers
    })
  } catch (error) {
    if (error?.name === 'AbortError') {
      throw error
    }
    throw new ApiError('暂时无法连接服务，请检查网络后重试。')
  }

  const contentType = response.headers.get('content-type') || ''
  let result
  if (contentType.includes('application/json')) {
    try {
      result = await response.json()
    } catch {
      result = { code: 'INVALID_RESPONSE', message: '服务返回了无法解析的数据。', data: null }
    }
  } else {
    const message = await response.text()
    result = {
      code: response.ok ? 'SUCCESS' : 'HTTP_ERROR',
      message: message || '请求失败',
      data: null
    }
  }

  if (response.status === 401 && auth) {
    clearStoredSession()
    if (emitUnauthorized) {
      window.dispatchEvent(new CustomEvent(UNAUTHORIZED_EVENT))
    }
  }

  if (!response.ok || result.code !== 'SUCCESS') {
    throw new ApiError(result.message || '请求失败，请稍后重试。', {
      status: response.status,
      code: result.code || 'HTTP_ERROR',
      data: result.data
    })
  }

  return result.data
}
