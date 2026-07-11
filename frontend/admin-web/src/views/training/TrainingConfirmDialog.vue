<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="tn-dialog-backdrop"
      @mousedown.self="cancel"
    >
      <section
        ref="dialogRef"
        class="tn-dialog"
        role="alertdialog"
        aria-modal="true"
        :aria-labelledby="titleId"
        :aria-describedby="descriptionId"
        @keydown.esc.prevent="cancel"
        @keydown.tab="trapFocus"
      >
        <header class="tn-dialog-header">
          <div>
            <p class="tn-dialog-eyebrow">
              请确认此操作
            </p>
            <h2 :id="titleId">
              {{ title }}
            </h2>
          </div>
          <button
            class="tn-dialog-close"
            type="button"
            :disabled="busy"
            aria-label="关闭确认对话框"
            @click="cancel"
          >
            关闭
          </button>
        </header>

        <p
          :id="descriptionId"
          class="tn-dialog-description"
        >
          {{ description }}
        </p>

        <div
          v-if="requireReason"
          class="tn-dialog-field"
        >
          <label :for="reasonId">
            {{ reasonLabel }} <span aria-hidden="true">*</span>
          </label>
          <textarea
            :id="reasonId"
            ref="reasonRef"
            :value="reason"
            :maxlength="reasonMaxLength"
            :placeholder="reasonPlaceholder"
            :aria-invalid="Boolean(reasonError)"
            :aria-describedby="reasonError ? reasonErrorId : undefined"
            :disabled="busy"
            rows="4"
            @input="$emit('update:reason', $event.target.value)"
          />
          <div class="tn-dialog-field-meta">
            <p
              v-if="reasonError"
              :id="reasonErrorId"
              role="alert"
            >
              {{ reasonError }}
            </p>
            <span>{{ reason.length }}/{{ reasonMaxLength }}</span>
          </div>
        </div>

        <p
          v-if="error"
          class="tn-dialog-error"
          role="alert"
        >
          {{ error }}
        </p>

        <footer class="tn-dialog-actions">
          <button
            ref="cancelRef"
            class="tn-dialog-button tn-dialog-button--secondary"
            type="button"
            :disabled="busy"
            @click="cancel"
          >
            {{ cancelLabel }}
          </button>
          <button
            class="tn-dialog-button"
            :class="tone === 'danger' ? 'tn-dialog-button--danger' : 'tn-dialog-button--primary'"
            type="button"
            :disabled="busy"
            @click="$emit('confirm')"
          >
            {{ busy ? '处理中…' : confirmLabel }}
          </button>
        </footer>
      </section>
    </div>
  </Teleport>
</template>

<script setup>
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  confirmLabel: {
    type: String,
    default: '确认'
  },
  cancelLabel: {
    type: String,
    default: '取消'
  },
  tone: {
    type: String,
    default: 'primary'
  },
  busy: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  },
  requireReason: {
    type: Boolean,
    default: false
  },
  reason: {
    type: String,
    default: ''
  },
  reasonLabel: {
    type: String,
    default: '操作原因'
  },
  reasonPlaceholder: {
    type: String,
    default: '请输入操作原因'
  },
  reasonError: {
    type: String,
    default: ''
  },
  reasonMaxLength: {
    type: Number,
    default: 500
  }
})

const emit = defineEmits(['cancel', 'confirm', 'update:reason'])
const dialogRef = ref(null)
const cancelRef = ref(null)
const reasonRef = ref(null)
const instanceId = `training-confirm-${Math.random().toString(36).slice(2)}`
const titleId = `${instanceId}-title`
const descriptionId = `${instanceId}-description`
const reasonId = `${instanceId}-reason`
const reasonErrorId = `${instanceId}-reason-error`
let previouslyFocusedElement = null

function cancel() {
  if (!props.busy) {
    emit('cancel')
  }
}

function focusableElements() {
  return [...(dialogRef.value?.querySelectorAll(
    'button:not(:disabled), textarea:not(:disabled), input:not(:disabled), select:not(:disabled), a[href]'
  ) || [])]
}

function trapFocus(event) {
  const elements = focusableElements()
  if (elements.length === 0) {
    event.preventDefault()
    return
  }

  const first = elements[0]
  const last = elements[elements.length - 1]
  if (event.shiftKey && document.activeElement === first) {
    event.preventDefault()
    last.focus()
  } else if (!event.shiftKey && document.activeElement === last) {
    event.preventDefault()
    first.focus()
  }
}

watch(() => props.open, async (isOpen) => {
  if (isOpen) {
    previouslyFocusedElement = document.activeElement
    await nextTick()
    if (props.requireReason) {
      reasonRef.value?.focus()
    } else {
      cancelRef.value?.focus()
    }
    return
  }

  previouslyFocusedElement?.focus?.()
  previouslyFocusedElement = null
})

onBeforeUnmount(() => previouslyFocusedElement?.focus?.())
</script>

<style scoped>
.tn-dialog-backdrop {
  position: fixed;
  z-index: 1000;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgb(8 29 27 / 58%);
}

.tn-dialog {
  --dialog-primary: #0f6b67;
  --dialog-danger: #b42318;
  width: min(100%, 520px);
  max-height: calc(100vh - 40px);
  max-height: calc(100dvh - 40px);
  overflow-y: auto;
  padding: 24px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 24px 80px rgb(2 20 18 / 30%);
  color: #162927;
}

.tn-dialog-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.tn-dialog-eyebrow {
  margin: 0 0 5px;
  color: var(--dialog-primary);
  font-size: 12px;
  font-weight: 750;
  letter-spacing: 0.08em;
}

.tn-dialog h2 {
  margin: 0;
  font-size: 22px;
  line-height: 1.3;
}

.tn-dialog-description {
  margin: 18px 0;
  color: #526765;
  line-height: 1.7;
}

.tn-dialog-close,
.tn-dialog-button {
  min-height: 44px;
  border-radius: 8px;
  font: inherit;
  font-weight: 650;
  cursor: pointer;
}

.tn-dialog-close {
  padding: 8px 10px;
  border: 0;
  background: transparent;
  color: #526765;
}

.tn-dialog-close:hover:not(:disabled) {
  background: #eef4f3;
  color: #162927;
}

.tn-dialog-field label {
  display: block;
  margin-bottom: 7px;
  font-size: 14px;
  font-weight: 700;
}

.tn-dialog-field label span,
.tn-dialog-field-meta p,
.tn-dialog-error {
  color: var(--dialog-danger);
}

.tn-dialog-field textarea {
  width: 100%;
  min-height: 104px;
  padding: 10px 12px;
  border: 1px solid #bdccca;
  border-radius: 8px;
  color: #162927;
  font: inherit;
  line-height: 1.5;
  resize: vertical;
}

.tn-dialog-field textarea[aria-invalid='true'] {
  border-color: var(--dialog-danger);
}

.tn-dialog-field-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 6px;
  color: #526765;
  font-size: 13px;
}

.tn-dialog-field-meta p {
  margin: 0;
}

.tn-dialog-error {
  margin: 14px 0 0;
  padding: 10px 12px;
  border-radius: 8px;
  background: #fef3f2;
  line-height: 1.5;
}

.tn-dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 24px;
}

.tn-dialog-button {
  padding: 9px 16px;
  border: 1px solid transparent;
}

.tn-dialog-button--secondary {
  border-color: #bdccca;
  background: #ffffff;
  color: #162927;
}

.tn-dialog-button--primary {
  background: var(--dialog-primary);
  color: #ffffff;
}

.tn-dialog-button--danger {
  background: var(--dialog-danger);
  color: #ffffff;
}

.tn-dialog button:focus-visible,
.tn-dialog textarea:focus-visible {
  outline: 3px solid rgb(15 107 103 / 30%);
  outline-offset: 2px;
}

.tn-dialog button:disabled,
.tn-dialog textarea:disabled {
  opacity: 0.48;
  cursor: not-allowed;
}

@media (max-width: 520px) {
  .tn-dialog {
    padding: 20px;
  }

  .tn-dialog-actions {
    align-items: stretch;
    flex-direction: column-reverse;
  }

  .tn-dialog-button {
    width: 100%;
  }
}
</style>
