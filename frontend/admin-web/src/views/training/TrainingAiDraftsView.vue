<template>
  <section class="page page-wide admin-module">
    <header class="page-heading ai-draft-heading">
      <div>
        <p class="eyebrow">
          AI辅助培训
        </p>
        <h2>AI题目草稿审核</h2>
        <p>完整核对题干、选项、答案与解析后再审核。通过后仅进入正式题库草稿，不会自动发布。</p>
      </div>
      <div
        class="draft-status-tabs"
        role="tablist"
        aria-label="草稿状态筛选"
      >
        <button
          v-for="item in statusOptions"
          :key="item.value"
          type="button"
          role="tab"
          :aria-selected="status === item.value"
          :class="{ 'is-active': status === item.value }"
          @click="selectStatus(item.value)"
        >
          {{ item.label }}
        </button>
      </div>
    </header>

    <div
      v-if="error"
      class="admin-alert"
    >
      {{ error }}
    </div>

    <form class="admin-record-card ai-generate-form" @submit.prevent="generate">
      <div><h3>从培训资料生成题目草稿</h3><p>选择已发布资料后，由当前 AI 实现生成草稿，仍需人工审核。</p></div>
      <label>培训资料<select v-model.number="generateForm.resourceId" required><option disabled value="">请选择资料</option><option v-for="resource in resources" :key="resource.id" :value="resource.id">{{ resource.title }}</option></select></label>
      <label>题型<select v-model="generateForm.questionType"><option value="SINGLE_CHOICE">单选题</option><option value="TRUE_FALSE">判断题</option></select></label>
      <label>数量<input v-model.number="generateForm.count" type="number" min="1" max="10"></label>
      <button class="button button-primary" :disabled="generating">{{ generating ? '正在生成…' : '生成草稿' }}</button>
    </form>

    <div class="admin-card-grid ai-draft-grid">
      <article
        v-for="draft in drafts"
        :key="draft.id"
        class="admin-record-card ai-draft-card"
      >
        <div class="admin-card-top">
          <span
            class="status-badge"
            :class="`draft-${draft.draftStatus?.toLowerCase()}`"
          >
            {{ statusLabel(draft.draftStatus) }}
          </span>
          <small>#{{ draft.id }}</small>
        </div>

        <h3>{{ draft.questionType === 'TRUE_FALSE' ? '判断题草稿' : '单选题草稿' }}</h3>
        <p class="ai-question-stem">
          {{ draft.questionContent }}
        </p>

        <ol class="ai-option-list">
          <li
            v-for="option in displayOptions(draft)"
            :key="option.label"
            :class="{ 'is-answer': isCorrectOption(draft, option) }"
          >
            <strong>{{ option.label }}</strong>
            <span>{{ option.content }}</span>
          </li>
        </ol>

        <dl class="ai-answer-details">
          <div>
            <dt>标准答案</dt>
            <dd>{{ answerLabel(draft) }}</dd>
          </div>
          <div>
            <dt>答案解析</dt>
            <dd>{{ draft.analysis || '审核时请补充答案解析。' }}</dd>
          </div>
          <div v-if="draft.reviewComment">
            <dt>审核意见</dt>
            <dd>{{ draft.reviewComment }}</dd>
          </div>
        </dl>

        <div class="admin-source-list ai-source-list">
          <span
            v-for="source in draft.sourceResources"
            :key="source.resourceId"
          >
            {{ source.title || `资料 #${source.resourceId}` }}
          </span>
        </div>

        <div
          v-if="draft.draftStatus === 'DRAFT'"
          class="admin-card-actions"
        >
          <button
            class="button button-quiet"
            @click="review(draft, 'REJECTED')"
          >
            驳回
          </button>
          <button
            class="button button-primary"
            @click="review(draft, 'APPROVED')"
          >
            审核通过
          </button>
        </div>
      </article>
    </div>

    <p
      v-if="!loading && !drafts.length"
      class="admin-empty"
    >
      当前状态下没有题目草稿
    </p>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { generateDrafts, listDrafts, reviewDraft } from '../../api/adminTraining.js'
import { listResources } from '../../api/training.js'

const statusOptions = [
  { value: 'DRAFT', label: '待审核' },
  { value: 'APPROVED', label: '已通过' },
  { value: 'REJECTED', label: '已驳回' },
]
const drafts = ref([])
const loading = ref(false)
const error = ref('')
const status = ref('DRAFT')
const resources = ref([])
const generating = ref(false)
const generateForm = ref({ resourceId: '', questionType: 'SINGLE_CHOICE', count: 1 })

const statusLabel = (value) => ({ DRAFT: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }[value] || value)

function displayOptions(draft) {
  if (draft.questionType === 'TRUE_FALSE') {
    return [{ label: 'A', content: '正确', value: 'true' }, { label: 'B', content: '错误', value: 'false' }]
  }
  return (draft.options || []).map((option) => ({
    label: option.label,
    content: option.content,
    value: option.label,
    correct: option.correct,
  }))
}

function isCorrectOption(draft, option) {
  return option.correct || String(option.value).toLowerCase() === String(draft.standardAnswer).toLowerCase()
}

function answerLabel(draft) {
  if (draft.questionType === 'TRUE_FALSE') {
    return String(draft.standardAnswer).toLowerCase() === 'true' ? '正确' : '错误'
  }
  const option = displayOptions(draft).find((item) => isCorrectOption(draft, item))
  return option ? `${option.label}. ${option.content}` : draft.standardAnswer
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const data = await listDrafts({ draftStatus: status.value, pageNo: 1, pageSize: 100 })
    drafts.value = data?.records || []
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function generate() {
  if (!generateForm.value.resourceId || generating.value) return
  generating.value = true
  error.value = ''
  try {
    await generateDrafts({ sourceResourceIds: [generateForm.value.resourceId], questionType: generateForm.value.questionType, count: generateForm.value.count })
    status.value = 'DRAFT'
    await load()
  } catch (e) {
    error.value = e.message
  } finally {
    generating.value = false
  }
}

function selectStatus(value) {
  if (status.value === value) return
  status.value = value
  load()
}

async function review(draft, reviewStatus) {
  let reviewComment = ''
  if (reviewStatus === 'REJECTED') {
    reviewComment = window.prompt('请输入驳回原因') ?? ''
    if (!reviewComment.trim()) return
  }
  try {
    await reviewDraft(draft.id, { reviewResult: reviewStatus, comment: reviewComment })
    await load()
  } catch (e) {
    error.value = e.message
  }
}

onMounted(async () => {
  await Promise.all([load(), listResources({ status: 'PUBLISHED', pageNo: 1, pageSize: 100 }).then((data) => { resources.value = data?.records || [] })])
})
</script>

<style scoped>
.ai-draft-heading { align-items: flex-end; }
.draft-status-tabs { display: flex; gap: 8px; padding: 5px; border: 1px solid var(--border); border-radius: 8px; background: #fff; }
.draft-status-tabs button { min-width: 88px; min-height: 38px; padding: 0 15px; border: 0; border-radius: 6px; color: var(--muted); background: transparent; font-weight: 700; cursor: pointer; }
.draft-status-tabs button:hover { color: var(--text); background: var(--surface-soft); }
.draft-status-tabs button.is-active { color: #fff; background: var(--primary); }
.ai-draft-grid { align-items: start; }
.ai-draft-card { display: grid; gap: 18px; }
.ai-question-stem { margin: 0; color: var(--text); font-size: 17px; font-weight: 700; line-height: 1.65; }
.ai-option-list { display: grid; gap: 9px; margin: 0; padding: 0; list-style: none; }
.ai-option-list li { display: grid; grid-template-columns: 30px 1fr; gap: 10px; align-items: center; padding: 11px 13px; border: 1px solid var(--border); border-radius: 7px; background: #fff; }
.ai-option-list li strong { display: grid; width: 28px; height: 28px; place-items: center; border-radius: 50%; color: var(--muted); background: var(--surface-soft); }
.ai-option-list li.is-answer { border-color: #8bd3b7; background: #effaf5; }
.ai-option-list li.is-answer strong { color: #fff; background: #16866f; }
.ai-answer-details { display: grid; gap: 12px; margin: 0; padding: 15px; border-left: 4px solid #d49b2c; background: #fff9e9; }
.ai-answer-details div { display: grid; grid-template-columns: 76px 1fr; gap: 12px; }
.ai-answer-details dt { color: var(--muted); font-size: 13px; font-weight: 700; }
.ai-answer-details dd { margin: 0; line-height: 1.65; }
.ai-source-list { margin-top: 0; }
.ai-generate-form { display: grid; grid-template-columns: minmax(240px, 1fr) repeat(2, minmax(140px, 190px)) 140px auto; gap: 16px; align-items: end; margin-bottom: 22px; }
.ai-generate-form p { margin: 6px 0 0; color: var(--muted); font-size: 13px; }
.ai-generate-form label { display: grid; gap: 7px; color: var(--muted); font-size: 13px; font-weight: 700; }
.ai-generate-form select, .ai-generate-form input { min-height: 42px; border: 1px solid var(--border); border-radius: 6px; padding: 0 11px; background: #fff; }
@media (max-width: 1100px) { .ai-generate-form { grid-template-columns: 1fr 1fr; } }
@media (max-width: 760px) { .ai-draft-heading { align-items: flex-start; } .draft-status-tabs { width: 100%; } .draft-status-tabs button { min-width: 0; flex: 1; } }
</style>
