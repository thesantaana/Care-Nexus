import { request } from './request.js'

export function login(credentials) {
  return request('/auth/login', {
    method: 'POST',
    body: credentials,
    skipAuth: true
  })
}

export function getCurrentUser() {
  return request('/auth/me')
}

export function logout() {
  return request('/auth/logout', {
    method: 'POST'
  })
}
