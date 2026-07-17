<template>
  <section class="assignment-page">
    <header class="page-heading">
      <p>COURSE ASSIGNMENT</p>
      <h1>发布课程作业</h1>
      <span>上传规范的 DOCX 文件，系统识别题目后由管理员确认发布。</span>
    </header>

    <section class="publish-panel">
      <div class="form-grid">
        <label>
          <span>选择课程</span>
          <select v-model.number="resourceId">
            <option disabled value="">请选择已发布课程</option>
            <option v-for="course in courses" :key="course.id" :value="course.id">
              {{ course.title }}
            </option>
          </select>
        </label>
        <label>
          <span>作业名称</span>
          <input v-model.trim="title" maxlength="120" placeholder="例如：手卫生课后作业">
        </label>
      </div>

      <div
        class="upload-zone"
        :class="{ dragging }"
        role="button"
        tabindex="0"
        @click="fileInput.click()"
        @keydown.enter="fileInput.click()"
        @dragover.prevent="dragging = true"
        @dragleave.prevent="dragging = false"
        @drop.prevent="handleDrop"
      >
        <AppIcon name="upload" />
        <strong>{{ fileName || '拖动 DOCX 文件到这里，或点击选择文件' }}</strong>
        <span>仅支持 .docx；单选题使用 A/B/C/D 选项，判断题答案填写“正确”或“错误”。</span>
        <input ref="fileInput" type="file" accept=".docx" hidden @change="handleChoose">
      </div>

      <div class="template-note">
        <strong>文档格式</strong>
        <code>1. 题干 / A. 选项一 / B. 选项二 / 答案：B / 解析：答案说明</code>
      </div>
      <p v-if="error" class="message error">{{ error }}</p>
      <button class="primary" type="button" :disabled="loading" @click="chooseOrPreview">
        {{ loading ? '正在识别…' : '识别题目' }}
      </button>
    </section>

    <section v-if="questions.length" class="preview-panel">
      <div class="preview-heading">
        <div>
          <h2>识别结果</h2>
          <p>共 {{ questions.length }} 题，请检查题干、选项、答案和解析。</p>
        </div>
        <span>{{ singleCount }} 道单选 · {{ booleanCount }} 道判断</span>
      </div>

      <article v-for="(question, questionIndex) in questions" :key="questionIndex" class="question-card">
        <div class="question-index">{{ questionIndex + 1 }}</div>
        <div class="question-editor">
          <div class="question-row">
            <select v-model="question.type" @change="normalizeOptions(question)">
              <option value="SINGLE_CHOICE">单选题</option>
              <option value="TRUE_FALSE">判断题</option>
            </select>
            <button class="icon-button" title="删除题目" type="button" @click="questions.splice(questionIndex, 1)">×</button>
          </div>
          <textarea v-model.trim="question.content" rows="2" placeholder="题干"></textarea>
          <div class="options">
            <label v-for="(option, optionIndex) in question.options" :key="optionIndex">
              <input v-model="question.standardAnswer" type="radio" :name="`answer-${questionIndex}`" :value="option">
              <span>{{ String.fromCharCode(65 + optionIndex) }}</span>
              <input v-model.trim="question.options[optionIndex]" :disabled="question.type === 'TRUE_FALSE'" @input="syncAnswer(question, option, optionIndex)">
            </label>
          </div>
          <textarea v-model.trim="question.analysis" rows="2" placeholder="答案解析（可补充）"></textarea>
        </div>
      </article>

      <div class="publish-actions">
        <p v-if="success" class="message success">{{ success }}</p>
        <p v-if="publishError" class="message error">{{ publishError }}</p>
        <button class="primary" type="button" :disabled="publishing" @click="confirmPublish">
          {{ publishing ? '正在发布…' : `确认发布 ${questions.length} 道题` }}
        </button>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import AppIcon from '../../components/AppIcon.vue'
import { listResources, previewAssignmentDocx, publishAssignment } from '../../api/training.js'

const courses = ref([])
const resourceId = ref('')
const title = ref('')
const selectedFile = ref(null)
const fileName = ref('')
const fileInput = ref(null)
const questions = ref([])
const loading = ref(false)
const publishing = ref(false)
const dragging = ref(false)
const error = ref('')
const publishError = ref('')
const success = ref('')
const singleCount = computed(() => questions.value.filter((item) => item.type === 'SINGLE_CHOICE').length)
const booleanCount = computed(() => questions.value.filter((item) => item.type === 'TRUE_FALSE').length)

onMounted(async () => {
  const page = await listResources({ status: 'PUBLISHED', pageNo: 1, pageSize: 100 })
  courses.value = page.records || page.items || []
})

function setFile(file) {
  error.value = ''
  if (!file || !file.name.toLowerCase().endsWith('.docx')) {
    selectedFile.value = null
    fileName.value = ''
    error.value = '请选择 .docx 文件，旧版 .doc 暂不支持。'
    return
  }
  selectedFile.value = file
  fileName.value = file.name
  if (!title.value) title.value = file.name.replace(/\.docx$/i, '')
}

function handleChoose(event) { setFile(event.target.files?.[0]) }
function handleDrop(event) { dragging.value = false; setFile(event.dataTransfer.files?.[0]) }
function chooseOrPreview() {
  if (!selectedFile.value) { fileInput.value?.click(); return }
  preview()
}

async function preview() {
  loading.value = true
  error.value = ''
  try {
    const result = await previewAssignmentDocx(selectedFile.value)
    questions.value = result.questions || []
  } catch (requestError) {
    error.value = requestError.message || '题目识别失败。'
  } finally { loading.value = false }
}

function normalizeOptions(question) {
  if (question.type === 'TRUE_FALSE') {
    question.options = ['正确', '错误']
    question.standardAnswer = '正确'
  } else if (question.options.length < 2) {
    question.options = ['选项一', '选项二', '选项三', '选项四']
    question.standardAnswer = question.options[0]
  }
}

function syncAnswer(question, oldValue, index) {
  if (question.standardAnswer === oldValue) question.standardAnswer = question.options[index]
}

async function confirmPublish() {
  publishError.value = ''
  success.value = ''
  if (!resourceId.value || !title.value || !questions.value.length) {
    publishError.value = '请选择课程、填写作业名称并确认题目。'
    return
  }
  publishing.value = true
  try {
    const result = await publishAssignment({ resourceId: resourceId.value, title: title.value, questions: questions.value })
    success.value = `作业发布成功，共发布 ${result.publishedCount} 道题。`
  } catch (requestError) {
    publishError.value = requestError.message || '作业发布失败。'
  } finally { publishing.value = false }
}
</script>

<style scoped>
.assignment-page { display: grid; gap: 24px; max-width: 1320px; margin: 0 auto; padding: 34px; color: #07162b; }
.page-heading p { margin: 0 0 8px; color: #087d75; font-size: 13px; font-weight: 800; letter-spacing: 3px; }
.page-heading h1 { margin: 0; font-size: 42px; }
.page-heading span { display: block; margin-top: 10px; color: #61738a; }
.publish-panel,.preview-panel { padding: 28px; border: 1px solid #d6e2e4; border-radius: 8px; background: #fff; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
label > span { display: block; margin-bottom: 8px; font-weight: 700; }
select,input,textarea { width: 100%; box-sizing: border-box; border: 1px solid #cbd9df; border-radius: 6px; padding: 12px 14px; font: inherit; }
.upload-zone { display: grid; justify-items: center; gap: 10px; margin-top: 22px; padding: 36px; border: 1px dashed #7db8b2; border-radius: 8px; background: #f3fbfa; cursor: pointer; }
.upload-zone.dragging { background: #e0f7f3; border-color: #087d75; }
.upload-zone .app-icon { width: 30px; height: 30px; color: #087d75; }
.upload-zone span,.template-note { color: #68798e; }
.template-note { display: flex; gap: 18px; margin: 18px 0; padding: 14px 16px; background: #f5f7f8; }
.primary { border: 0; border-radius: 6px; padding: 13px 22px; background: #087d75; color: #fff; font-weight: 800; cursor: pointer; }
.primary:disabled { opacity: .55; cursor: wait; }
.preview-heading,.question-row,.publish-actions { display: flex; align-items: center; justify-content: space-between; gap: 18px; }
.preview-heading h2 { margin: 0; }
.question-card { display: grid; grid-template-columns: 44px 1fr; gap: 16px; padding: 24px 0; border-top: 1px solid #e5ecef; }
.question-index { display: grid; place-items: center; width: 38px; height: 38px; border-radius: 50%; background: #dff7f3; color: #087d75; font-weight: 800; }
.question-editor { display: grid; gap: 14px; }
.question-row select { width: 160px; }
.icon-button { border: 0; background: transparent; font-size: 26px; cursor: pointer; }
.options { display: grid; gap: 10px; }
.options label { display: grid; grid-template-columns: 20px 24px 1fr; align-items: center; gap: 8px; }
.options input[type='radio'] { width: 18px; }
.message { margin: 12px 0; }.error { color: #b42318; }.success { color: #087d75; }
@media (max-width: 760px) { .assignment-page { padding: 18px; }.form-grid { grid-template-columns: 1fr; } }
</style>
