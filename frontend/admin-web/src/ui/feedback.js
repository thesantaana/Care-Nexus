import { reactive } from 'vue'

let nextId = 1

export const feedback = reactive({
  messages: []
})

export function dismissMessage(id) {
  const index = feedback.messages.findIndex((message) => message.id === id)
  if (index >= 0) {
    feedback.messages.splice(index, 1)
  }
}

export function notify(message, type = 'success') {
  const id = nextId
  nextId += 1
  feedback.messages.push({ id, message, type })
  window.setTimeout(() => dismissMessage(id), 4500)
}
