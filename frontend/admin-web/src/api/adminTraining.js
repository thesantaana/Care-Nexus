import { request } from './request.js'

export const listExams = () => request('/training/exams')
export const createExam = (payload) => request('/training/exams', { method: 'POST', body: payload })
export const publishExam = (id) => request(`/training/exams/${id}/publish`, { method: 'PUT' })
export const listDrafts = (query = {}) => request('/training/ai/question-drafts', { query })
export const reviewDraft = (id, payload) => request(`/training/ai/question-drafts/${id}/review`, { method: 'PUT', body: payload })
export const listCaregiverScores = () => request('/training/learning/scores/users')
