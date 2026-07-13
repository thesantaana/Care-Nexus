import { request } from './request.js'

export function getTrainingCategories() {
  return request('/training/categories', { query: { status: 'ENABLED' } })
}

export function getTrainingTags() {
  return request('/training/tags', { query: { status: 'ENABLED' } })
}

export function getTrainingResources(filters = {}, signal) {
  return request('/training/resources', {
    query: {
      ...filters,
      status: 'PUBLISHED'
    },
    signal
  })
}

export function getTrainingResource(resourceId, signal) {
  return request(`/training/resources/${resourceId}`, { signal })
}

export function reportLearningAccess(resourceId, accessSeconds) {
  return request('/training/learning/access', {
    method: 'POST',
    json: { resourceId, accessSeconds }
  })
}

export function getMyLearningRecord(signal) {
  return request('/training/learning/me', { signal })
}

export function askTrainingAi(resourceId, question) {
  return request('/training/ai/qa', {
    method: 'POST',
    json: { sourceResourceIds: [Number(resourceId)], question }
  })
}

export function summarizeTrainingResource(resourceId) {
  return request('/training/ai/summary', {
    method: 'POST',
    json: { sourceResourceIds: [Number(resourceId)] }
  })
}

export function getTrainingSuggestions(resourceId) {
  return request('/training/ai/suggestions', {
    method: 'POST',
    json: { sourceResourceIds: [Number(resourceId)] }
  })
}
