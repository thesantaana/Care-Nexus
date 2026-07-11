export const TOKEN_STORAGE_KEY = 'care-nexus-token'
export const USER_STORAGE_KEY = 'care-nexus-user'
export const UNAUTHORIZED_EVENT = 'care-nexus:unauthorized'

const baseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(message, { code = 'REQUEST_ERROR', status = 0, data = null } = {}) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
    this.data = data
  }
}

function buildUrl(path, query) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const url = new URL(`${baseUrl}${normalizedPath}`, window.location.origin)

  Object.entries(query || {}).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) {
      url.searchParams.set(key, String(value))
    }
  })

  return url.toString()
}

function clearStoredSession() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
  localStorage.removeItem(USER_STORAGE_KEY)
}

async function readResponse(response) {
  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return response.json()
  }

  const message = await response.text()
  return {
    code: response.ok ? 'SUCCESS' : 'HTTP_ERROR',
    message,
    data: null
  }
}

export async function request(path, options = {}) {
  const {
    body: requestBody,
    headers: requestHeaders,
    query,
    skipAuth = false,
    ...fetchOptions
  } = options
  const headers = new Headers(requestHeaders || {})
  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  let body = requestBody

  if (token && !skipAuth) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  if (body && !(body instanceof FormData)) {
    if (!headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json')
    }
    if (typeof body !== 'string') {
      body = JSON.stringify(body)
    }
  }

  let response
  try {
    response = await fetch(buildUrl(path, query), {
      ...fetchOptions,
      body,
      headers
    })
  } catch (error) {
    throw new ApiError('无法连接服务器，请检查网络或后端服务状态', {
      code: 'NETWORK_ERROR',
      data: error
    })
  }

  const result = await readResponse(response)

  if (response.status === 401 && !skipAuth) {
    clearStoredSession()
    window.dispatchEvent(new CustomEvent(UNAUTHORIZED_EVENT, {
      detail: { path }
    }))
  }
  if (!response.ok || result.code !== 'SUCCESS') {
    throw new ApiError(result.message || '请求失败', {
      code: result.code,
      status: response.status,
      data: result.data
    })
  }

  return result.data
}
