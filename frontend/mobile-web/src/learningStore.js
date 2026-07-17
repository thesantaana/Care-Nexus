import { reactive } from 'vue'
import { getLearningLibrary } from './api/training.js'

function emptyState() {
  return { visited: [], completed: [] }
}

export const learningLibrary = reactive(emptyState())

export async function loadLearningLibrary() {
  const server = await getLearningLibrary()
  Object.assign(learningLibrary, {
    visited: (server.courseResourceIds || []).map(Number),
    completed: (server.completedResourceIds || []).map(Number)
  })
}

export function seedDemoLearningLibrary(resourceIds) {
  void resourceIds
}
