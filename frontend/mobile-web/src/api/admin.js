import { request } from './request.js'

export const listAdminResources = (query = {}) => request('/training/resources', { query })
export const listAdminExams = () => request('/training/exams')
export const listAdminDrafts = (query = {}) => request('/training/ai/question-drafts', { query })
export const reviewAdminDraft = (id, reviewResult, comment = '') => request(`/training/ai/question-drafts/${id}/review`, {
  method: 'PUT',
  json: { reviewResult, comment }
})
export const listAdminScores = () => request('/training/learning/scores/users')
