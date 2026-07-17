<template>
  <section class="admin-section-page">
    <header>
      <RouterLink class="back-link" to="/workspace"><AppIcon name="arrow-left" />管理工作台</RouterLink>
      <p class="eyebrow">{{ meta.eyebrow }}</p>
      <h1 tabindex="-1">{{ meta.title }}</h1>
      <p>{{ meta.description }}</p>
    </header>

    <p v-if="loading" class="state-card">正在读取管理数据…</p>
    <p v-else-if="error" class="state-card is-error">{{ error }}</p>

    <div v-else-if="section === 'resources'" class="record-list">
      <article v-for="item in records" :key="item.id">
        <div><span class="record-type">{{ item.categoryName || '护理培训' }}</span><strong>{{ item.title }}</strong><small>{{ item.resourceType }} · {{ statusText(item.status) }}</small></div>
        <span class="status-pill">{{ statusText(item.status) }}</span>
      </article>
    </div>

    <div v-else-if="section === 'exams'" class="record-list">
      <article v-for="item in records" :key="item.id">
        <div><span class="record-type">课程考核</span><strong>{{ item.examName || item.title || `考核 #${item.id}` }}</strong><small>及格分 {{ item.passScore || 60 }} · {{ statusText(item.status) }}</small></div>
        <span class="status-pill">{{ statusText(item.status) }}</span>
      </article>
    </div>

    <div v-else-if="section === 'ai-drafts'" class="draft-list">
      <article v-for="item in records" :key="item.id">
        <span class="record-type">{{ item.questionType === 'TRUE_FALSE' ? '判断题草稿' : '单选题草稿' }}</span>
        <strong>{{ item.questionContent }}</strong>
        <small>{{ statusText(item.draftStatus) }}</small>
        <div v-if="item.draftStatus === 'DRAFT'" class="draft-actions">
          <button class="quiet-button" type="button" @click="review(item, 'REJECTED')">驳回</button>
          <button class="primary-button" type="button" @click="review(item, 'APPROVED')">审核通过</button>
        </div>
      </article>
    </div>

    <div v-else-if="section === 'scores'" class="record-list">
      <article v-for="item in records" :key="item.userId || item.username">
        <div><span class="record-type">护工培训</span><strong>{{ item.displayName || item.realName || item.username }}</strong><small>平均分 {{ item.averageScore ?? 0 }} · 通过 {{ item.passedCourseCount ?? 0 }} 门</small></div>
        <span class="status-pill" :class="{ passed: item.trainingPassed }">{{ item.trainingPassed ? '已通过' : '培训中' }}</span>
      </article>
    </div>

    <div v-else class="assignment-card">
      <span class="admin-action-icon"><AppIcon name="file" /></span>
      <h2>DOCX 作业发布</h2>
      <p>上传 DOCX、校对识别题目并发布到指定课程属于精细编辑流程，建议在网页管理端完成。</p>
      <a href="http://localhost:5173/training/assignments/publish">打开网页端发布作业<AppIcon name="external" /></a>
    </div>

    <p v-if="!loading && !error && section !== 'assignments' && !records.length" class="state-card">当前没有相关数据</p>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { listAdminDrafts, listAdminExams, listAdminResources, listAdminScores, reviewAdminDraft } from '../api/admin.js'

const props = defineProps({ section: { type: String, required: true } })
const records = ref([])
const loading = ref(false)
const error = ref('')
const sectionMeta = {
  resources: { eyebrow: 'COURSE MANAGEMENT', title: '课程管理', description: '查看护理培训课程、内容类型与发布状态。' },
  assignments: { eyebrow: 'ASSIGNMENT PUBLISHING', title: '发布作业', description: '管理课程作业的识别、校对和发布流程。' },
  exams: { eyebrow: 'EXAM MANAGEMENT', title: '题库与考核', description: '查看课程考核、及格规则和发布状态。' },
  'ai-drafts': { eyebrow: 'AI REVIEW', title: 'AI 草稿审核', description: '审核基于已发布护理资料生成的题目草稿。' },
  scores: { eyebrow: 'TRAINING RESULTS', title: '培训成绩', description: '查看护工课程成绩和整体培训状态。' }
}
const meta = computed(() => sectionMeta[props.section] || sectionMeta.resources)
const statusText = (value) => ({ PUBLISHED: '已发布', DRAFT: '待审核', APPROVED: '已通过', REJECTED: '已驳回', OFFLINE: '已下架' }[value] || value || '未知')

async function load() {
  if (props.section === 'assignments') return
  loading.value = true
  error.value = ''
  try {
    const loaders = {
      resources: () => listAdminResources({ pageNo: 1, pageSize: 100 }),
      exams: listAdminExams,
      'ai-drafts': () => listAdminDrafts({ pageNo: 1, pageSize: 100 }),
      scores: listAdminScores
    }
    const data = await loaders[props.section]()
    records.value = data?.records || data || []
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function review(item, result) {
  let comment = ''
  if (result === 'REJECTED') {
    comment = window.prompt('请输入驳回原因') ?? ''
    if (!comment.trim()) return
  }
  try {
    await reviewAdminDraft(item.id, result, comment)
    await load()
  } catch (e) {
    error.value = e.message
  }
}

onMounted(load)
watch(() => props.section, load)
</script>

<style scoped>
.admin-section-page { display: grid; gap: 22px; padding: 24px 18px 108px; }
.admin-section-page header { padding: 22px; border: 1px solid #cbdde1; border-radius: 22px; background: linear-gradient(145deg,#fff,#e8f4f2); }
.admin-section-page h1 { margin: 10px 0; font-size: 34px; letter-spacing: -.05em; }
.admin-section-page header p { margin-bottom: 0; color: var(--color-muted); line-height: 1.7; }
.back-link { display: inline-flex; align-items: center; gap: 5px; margin-bottom: 22px; color: var(--color-primary); font-size: 13px; font-weight: 800; text-decoration: none; }
.record-list,.draft-list { display: grid; gap: 12px; }
.record-list article,.draft-list article,.assignment-card,.state-card { padding: 18px; border: 1px solid var(--color-border); border-radius: 17px; background: #fff; box-shadow: var(--shadow-sm); }
.record-list article { display: flex; align-items: center; justify-content: space-between; gap: 14px; }
.record-list article div,.draft-list article { min-width: 0; }
.record-list strong,.record-list small,.draft-list strong,.draft-list small { display: block; }
.record-list strong,.draft-list strong { margin-top: 7px; line-height: 1.5; }
.record-list small,.draft-list small { margin-top: 6px; color: var(--color-muted); line-height: 1.5; }
.record-type { color: var(--color-primary); font-size: 11px; font-weight: 900; letter-spacing: .08em; }
.status-pill { flex: 0 0 auto; padding: 6px 9px; border-radius: 999px; color: #8a5b10; background: #fff3d9; font-size: 11px; font-weight: 800; }
.status-pill.passed { color: var(--color-success); background: #e7f8ef; }
.draft-actions { display: flex; justify-content: flex-end; gap: 9px; margin-top: 16px; }
.draft-actions button { min-height: 40px; padding: 0 14px; border-radius: 10px; font-weight: 800; }
.quiet-button { border: 1px solid var(--color-border); background: #fff; }
.primary-button { border: 0; color: #fff; background: var(--color-primary); }
.assignment-card { text-align: center; }
.assignment-card .admin-action-icon { display: grid; width: 58px; height: 58px; margin: 0 auto; place-items: center; border-radius: 16px; color: var(--color-primary); background: var(--color-primary-soft); }
.assignment-card p { color: var(--color-muted); line-height: 1.8; }
.assignment-card a { display: inline-flex; min-height: 44px; align-items: center; gap: 8px; padding: 0 16px; border-radius: 12px; color: #fff; background: var(--color-primary); font-weight: 800; text-decoration: none; }
.state-card { margin: 0; color: var(--color-muted); text-align: center; }
.state-card.is-error { color: var(--color-danger); background: var(--color-danger-soft); }
</style>
