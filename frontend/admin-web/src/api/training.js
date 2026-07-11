import { request } from './request.js'

export function listCategories(status = '') {
  return request('/training/categories', {
    query: { status }
  })
}

export function createCategory(payload) {
  return request('/training/categories', {
    method: 'POST',
    body: payload
  })
}

export function updateCategory(id, payload) {
  return request(`/training/categories/${id}`, {
    method: 'PUT',
    body: payload
  })
}

export function updateCategoryStatus(id, status) {
  return request(`/training/categories/${id}/status`, {
    method: 'PUT',
    body: { status }
  })
}

export function listTags(status = '') {
  return request('/training/tags', {
    query: { status }
  })
}

export function createTag(payload) {
  return request('/training/tags', {
    method: 'POST',
    body: payload
  })
}

export function updateTag(id, payload) {
  return request(`/training/tags/${id}`, {
    method: 'PUT',
    body: payload
  })
}

export function updateTagStatus(id, status) {
  return request(`/training/tags/${id}/status`, {
    method: 'PUT',
    body: { status }
  })
}

export function listResources(filters = {}) {
  return request('/training/resources', {
    query: filters
  })
}

export function getResource(id) {
  return request(`/training/resources/${id}`)
}

export function createResource(payload) {
  return request('/training/resources', {
    method: 'POST',
    body: payload
  })
}

export function updateResource(id, payload) {
  return request(`/training/resources/${id}`, {
    method: 'PUT',
    body: payload
  })
}

export function publishResource(id) {
  return request(`/training/resources/${id}/publish`, {
    method: 'PUT'
  })
}

export function offlineResource(id, reason) {
  return request(`/training/resources/${id}/offline`, {
    method: 'PUT',
    body: { reason }
  })
}
