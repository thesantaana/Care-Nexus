import { computed, reactive } from 'vue'
import { sessionState } from './session.js'

const STORAGE_PREFIX = 'carenexus-learning-library'

function emptyState() {
  return { favorites: [], visited: [], completed: [], notes: {} }
}

function storageKey() {
  return `${STORAGE_PREFIX}:${sessionState.user?.userId || 'guest'}`
}

function readState() {
  try {
    return { ...emptyState(), ...JSON.parse(localStorage.getItem(storageKey()) || '{}') }
  } catch {
    return emptyState()
  }
}

export const learningLibrary = reactive(emptyState())

export function loadLearningLibrary() {
  Object.assign(learningLibrary, readState())
}

function persist() {
  localStorage.setItem(storageKey(), JSON.stringify(learningLibrary))
}

function toggle(listName, resourceId) {
  const id = Number(resourceId)
  const list = learningLibrary[listName]
  const index = list.indexOf(id)
  if (index >= 0) list.splice(index, 1)
  else list.push(id)
  persist()
}

export function toggleFavorite(resourceId) {
  toggle('favorites', resourceId)
}

export function toggleCompleted(resourceId) {
  toggle('completed', resourceId)
}

export function markVisited(resourceId) {
  const id = Number(resourceId)
  if (!learningLibrary.visited.includes(id)) {
    learningLibrary.visited.push(id)
    persist()
  }
}

export function saveNote(resourceId, content, title = '') {
  const id = String(resourceId)
  const trimmed = content.trim()
  if (!trimmed) delete learningLibrary.notes[id]
  else learningLibrary.notes[id] = { resourceId: Number(resourceId), title, content: trimmed, updatedAt: new Date().toISOString() }
  persist()
}

export const favoriteCount = computed(() => learningLibrary.favorites.length)
