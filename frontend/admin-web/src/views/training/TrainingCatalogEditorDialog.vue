<template>
  <Teleport to="body">
    <div
      v-if="open"
      class="catalog-dialog-backdrop"
      @mousedown.self="cancel"
    >
      <section
        ref="dialogRef"
        class="catalog-dialog"
        role="dialog"
        aria-modal="true"
        :aria-labelledby="titleId"
        @keydown.esc.prevent="cancel"
        @keydown.tab="trapFocus"
      >
        <header>
          <div>
            <p>{{ item ? '编辑基础数据' : '新增基础数据' }}</p>
            <h2 :id="titleId">
              {{ dialogTitle }}
            </h2>
          </div>
          <button
            type="button"
            :disabled="busy"
            aria-label="关闭编辑对话框"
            @click="cancel"
          >
            关闭
          </button>
        </header>

        <form
          novalidate
          @submit.prevent="submit"
        >
          <div class="catalog-dialog-field">
            <label :for="nameId">
              {{ kind === 'category' ? '类别名称' : '标签名称' }}
              <span aria-hidden="true">*</span>
            </label>
            <input
              :id="nameId"
              ref="nameRef"
              v-model="form.name"
              type="text"
              maxlength="128"
              autocomplete="off"
              :disabled="busy"
              :aria-invalid="Boolean(errors.name)"
              :aria-describedby="errors.name ? nameErrorId : undefined"
            >
            <div class="catalog-dialog-meta">
              <p
                v-if="errors.name"
                :id="nameErrorId"
                role="alert"
              >
                {{ errors.name }}
              </p>
              <span>{{ form.name.length }}/128</span>
            </div>
          </div>

          <div
            v-if="kind === 'category'"
            class="catalog-dialog-field"
          >
            <label :for="sortId">
              排序值 <span aria-hidden="true">*</span>
            </label>
            <input
              :id="sortId"
              v-model="form.sortNo"
              type="number"
              min="0"
              max="9999"
              step="1"
              inputmode="numeric"
              :disabled="busy"
              :aria-invalid="Boolean(errors.sortNo)"
              :aria-describedby="errors.sortNo ? sortErrorId : `${sortId}-help`"
            >
            <p
              :id="`${sortId}-help`"
              class="catalog-dialog-help"
            >
              数字越小越靠前，范围 0–9999。
            </p>
            <p
              v-if="errors.sortNo"
              :id="sortErrorId"
              class="catalog-dialog-error"
              role="alert"
            >
              {{ errors.sortNo }}
            </p>
          </div>

          <p
            v-if="error"
            class="catalog-dialog-submit-error"
            role="alert"
          >
            {{ error }}
          </p>

          <footer>
            <button
              ref="cancelRef"
              class="catalog-dialog-secondary"
              type="button"
              :disabled="busy"
              @click="cancel"
            >
              取消
            </button>
            <button
              class="catalog-dialog-primary"
              type="submit"
              :disabled="busy"
            >
              {{ busy ? '保存中…' : '保存' }}
            </button>
          </footer>
        </form>
      </section>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  kind: {
    type: String,
    required: true
  },
  item: {
    type: Object,
    default: null
  },
  busy: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['cancel', 'submit'])
const form = reactive({ name: '', sortNo: 0 })
const errors = reactive({ name: '', sortNo: '' })
const dialogRef = ref(null)
const nameRef = ref(null)
const cancelRef = ref(null)
const instanceId = `catalog-editor-${Math.random().toString(36).slice(2)}`
const titleId = `${instanceId}-title`
const nameId = `${instanceId}-name`
const nameErrorId = `${instanceId}-name-error`
const sortId = `${instanceId}-sort`
const sortErrorId = `${instanceId}-sort-error`
let previouslyFocusedElement = null

const dialogTitle = computed(() => {
  const action = props.item ? '编辑' : '新增'
  return `${action}${props.kind === 'category' ? '培训类别' : '培训标签'}`
})

function resetForm() {
  form.name = props.kind === 'category'
    ? props.item?.categoryName || ''
    : props.item?.tagName || ''
  form.sortNo = props.item?.sortNo ?? 0
  errors.name = ''
  errors.sortNo = ''
}

function validate() {
  const name = form.name.trim()
  errors.name = name ? '' : '请输入名称。'
  if (name.length > 128) {
    errors.name = '名称不能超过 128 个字符。'
  }

  if (props.kind === 'category') {
    const sortNo = Number(form.sortNo)
    errors.sortNo = Number.isInteger(sortNo) && sortNo >= 0 && sortNo <= 9999
      ? ''
      : '排序值必须是 0–9999 之间的整数。'
  } else {
    errors.sortNo = ''
  }

  return !errors.name && !errors.sortNo
}

function submit() {
  if (!validate()) {
    nextTick(() => {
      const invalidField = dialogRef.value?.querySelector('[aria-invalid="true"]')
      invalidField?.focus()
    })
    return
  }

  emit('submit', props.kind === 'category'
    ? { categoryName: form.name.trim(), sortNo: Number(form.sortNo) }
    : { tagName: form.name.trim() })
}

function cancel() {
  if (!props.busy) {
    emit('cancel')
  }
}

function trapFocus(event) {
  const elements = [...(dialogRef.value?.querySelectorAll(
    'button:not(:disabled), input:not(:disabled), select:not(:disabled), textarea:not(:disabled), a[href]'
  ) || [])]
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
  if (!isOpen) {
    previouslyFocusedElement?.focus?.()
    previouslyFocusedElement = null
    return
  }
  previouslyFocusedElement = document.activeElement
  resetForm()
  await nextTick()
  nameRef.value?.focus()
})

watch(() => props.item, resetForm)
onBeforeUnmount(() => previouslyFocusedElement?.focus?.())
</script>

<style scoped>
.catalog-dialog-backdrop {
  position: fixed;
  z-index: 1000;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgb(8 29 27 / 58%);
}

.catalog-dialog {
  width: min(100%, 500px);
  max-height: calc(100vh - 40px);
  max-height: calc(100dvh - 40px);
  overflow-y: auto;
  padding: 24px;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 24px 80px rgb(2 20 18 / 30%);
  color: #162927;
}

.catalog-dialog header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 22px;
}

.catalog-dialog header p {
  margin: 0 0 5px;
  color: #0f6b67;
  font-size: 12px;
  font-weight: 750;
  letter-spacing: 0.08em;
}

.catalog-dialog h2 {
  margin: 0;
  font-size: 22px;
}

.catalog-dialog button,
.catalog-dialog input {
  min-height: 44px;
  border-radius: 8px;
  font: inherit;
}

.catalog-dialog header button {
  padding: 8px 10px;
  border: 0;
  background: transparent;
  color: #526765;
  font-weight: 650;
  cursor: pointer;
}

.catalog-dialog-field {
  margin-bottom: 18px;
}

.catalog-dialog-field label {
  display: block;
  margin-bottom: 7px;
  font-size: 14px;
  font-weight: 700;
}

.catalog-dialog-field label span,
.catalog-dialog-error,
.catalog-dialog-meta p,
.catalog-dialog-submit-error {
  color: #b42318;
}

.catalog-dialog-field input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #bdccca;
  color: #162927;
}

.catalog-dialog-field input[aria-invalid='true'] {
  border-color: #b42318;
}

.catalog-dialog-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 6px;
  color: #526765;
  font-size: 13px;
}

.catalog-dialog-meta p,
.catalog-dialog-help,
.catalog-dialog-error {
  margin: 6px 0 0;
  font-size: 13px;
}

.catalog-dialog-help {
  color: #526765;
}

.catalog-dialog-submit-error {
  padding: 10px 12px;
  border-radius: 8px;
  background: #fef3f2;
  line-height: 1.5;
}

.catalog-dialog footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 24px;
}

.catalog-dialog footer button {
  padding: 9px 16px;
  font-weight: 650;
  cursor: pointer;
}

.catalog-dialog-secondary {
  border: 1px solid #bdccca;
  background: #ffffff;
  color: #162927;
}

.catalog-dialog-primary {
  border: 1px solid #0f6b67;
  background: #0f6b67;
  color: #ffffff;
}

.catalog-dialog button:focus-visible,
.catalog-dialog input:focus-visible {
  outline: 3px solid rgb(15 107 103 / 30%);
  outline-offset: 2px;
}

.catalog-dialog button:disabled,
.catalog-dialog input:disabled {
  opacity: 0.48;
  cursor: not-allowed;
}

@media (max-width: 520px) {
  .catalog-dialog {
    padding: 20px;
  }

  .catalog-dialog footer {
    align-items: stretch;
    flex-direction: column-reverse;
  }
}
</style>
