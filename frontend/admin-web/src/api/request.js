const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

export async function request(path, options = {}) {
  const token = localStorage.getItem('care-nexus-token')
  const headers = new Headers(options.headers || {})
  const body = options.body

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }
  if (body && !(body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(`${baseUrl}${path}`, {
    ...options,
    headers
  })

  const contentType = response.headers.get('content-type') || ''
  const result = contentType.includes('application/json')
    ? await response.json()
    : { code: response.ok ? 'SUCCESS' : 'HTTP_ERROR', message: await response.text(), data: null }

  if (response.status === 401) {
    localStorage.removeItem('care-nexus-token')
    localStorage.removeItem('care-nexus-user')
  }
  if (!response.ok || result.code !== 'SUCCESS') {
    throw new Error(result.message || '请求失败')
  }
  return result.data
}
