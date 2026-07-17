import { request } from './request.js'

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

export function getLearningLibrary(signal) {
  return request('/training/learning/library', { signal })
}

export function getTrainingNotes(signal) {
  return request('/training/notes', { signal })
}

export function getTrainingNote(resourceId, signal) {
  return request(`/training/notes/resource/${resourceId}`, { signal })
}

export function saveTrainingNote(resourceId, title, content) {
  return request(`/training/notes/resource/${resourceId}`, {
    method: 'PUT',
    json: { title, content }
  })
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

export function getCourseLearningStatus(resourceId) {
  return request(`/training/learning/resources/${resourceId}`)
}

export function getTrainingScores() {
  return request('/training/learning/scores')
}

export function getCourseDiscussions(resourceId, sort = 'LATEST') {
  return request(`/training/resources/${resourceId}/discussions`, { query: { sort } })
}

export function createCourseDiscussion(resourceId, title, content) {
  return request(`/training/resources/${resourceId}/discussions`, { method: 'POST', json: { title, content } })
}

export function getDiscussionReplies(discussionId) {
  return request(`/training/discussions/${discussionId}/replies`)
}

export function createDiscussionReply(discussionId, content, parentReplyId = null) {
  return request(`/training/discussions/${discussionId}/replies`, { method: 'POST', json: { content, parentReplyId } })
}

export function toggleDiscussionLike(discussionId) {
  return request(`/training/discussions/${discussionId}/like`, { method: 'PUT' })
}

export function getCourseAssignments(resourceId) {
  return request(`/training/resources/${resourceId}/assignments`)
}

export function submitAssignment(assignmentId, answer) {
  return request(`/training/assignments/${assignmentId}/submission`, { method: 'POST', json: { answer } })
}

export function getTrainingExams() {
  return request('/training/exams')
}

export function getTrainingExam(examId) {
  return request(`/training/exams/${examId}`)
}

export function submitTrainingExam(examId, answers) {
  return request(`/training/exams/${examId}/records`, { method: 'POST', json: { answers } })
}

export function getCourseMistakes(resourceId) {
  return request(`/training/learning/resources/${resourceId}/mistakes`)
}
