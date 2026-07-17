<template>
  <div class="admin-mobile-dashboard">
    <header class="admin-mobile-hero">
      <div>
        <p class="eyebrow">管理概览</p>
        <h1 tabindex="-1">{{ greeting }}，{{ sessionState.user?.displayName }}</h1>
        <p>集中管理护理培训课程、考核、AI 题目草稿和护工培训结果。</p>
      </div>
      <span class="admin-role-badge"><AppIcon name="services" />管理员</span>
    </header>

    <section class="admin-mobile-metrics" aria-label="管理数据概览">
      <article><strong>{{ stats.resources }}</strong><span>培训课程</span></article>
      <article><strong>{{ stats.exams }}</strong><span>课程考核</span></article>
      <article><strong>{{ stats.pendingDrafts }}</strong><span>待审核草稿</span></article>
      <article><strong>{{ stats.passedCaregivers }}</strong><span>通过培训</span></article>
    </section>

    <p v-if="error" class="admin-mobile-alert">{{ error }}</p>

    <section aria-labelledby="admin-actions-title">
      <div class="section-heading">
        <div><p class="section-kicker">MANAGEMENT</p><h2 id="admin-actions-title">移动管理工作台</h2></div>
      </div>
      <div class="admin-mobile-actions">
        <RouterLink v-for="card in cards" :key="card.to" :to="card.to">
          <span class="admin-action-icon"><AppIcon :name="card.icon" /></span>
          <span><strong>{{ card.title }}</strong><small>{{ card.description }}</small></span>
          <AppIcon name="arrow-right" />
        </RouterLink>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppIcon from './AppIcon.vue'
import { listAdminDrafts, listAdminExams, listAdminResources, listAdminScores } from '../api/admin.js'
import { sessionState } from '../session.js'

const hour = new Date().getHours()
const greeting = hour < 12 ? '早上好' : hour < 18 ? '下午好' : '晚上好'
const stats = reactive({ resources: 0, exams: 0, pendingDrafts: 0, passedCaregivers: 0 })
const error = ref('')
const cards = [
  { title: '课程管理', description: '查看课程内容、类型和发布状态', to: '/admin/resources', icon: 'book' },
  { title: '发布作业', description: '管理课程作业与发布流程', to: '/admin/assignments', icon: 'file' },
  { title: '题库与考核', description: '查看课程考核和及格规则', to: '/admin/exams', icon: 'note' },
  { title: 'AI 草稿审核', description: '审核基于培训资料生成的题目', to: '/admin/ai-drafts', icon: 'pulse' },
  { title: '培训成绩', description: '查看护工成绩与培训通过状态', to: '/admin/scores', icon: 'progress' }
]

onMounted(async () => {
  try {
    const [resources, exams, drafts, scores] = await Promise.all([
      listAdminResources({ pageNo: 1, pageSize: 1 }),
      listAdminExams(),
      listAdminDrafts({ draftStatus: 'DRAFT', pageNo: 1, pageSize: 1 }),
      listAdminScores()
    ])
    stats.resources = resources?.total || 0
    stats.exams = exams?.length || 0
    stats.pendingDrafts = drafts?.total || 0
    stats.passedCaregivers = scores?.filter((item) => item.trainingPassed).length || 0
  } catch (e) {
    error.value = e.message
  }
})
</script>

<style scoped>
.admin-mobile-dashboard { display: grid; gap: 28px; padding: 24px 18px 108px; }
.admin-mobile-hero { display: grid; gap: 22px; padding: 28px 24px; border: 1px solid #c7dce0; border-radius: 24px; background: linear-gradient(145deg,#f9ffff,#e1f1ee 58%,#dbeafe); box-shadow: var(--shadow-sm); }
.admin-mobile-hero h1 { max-width: 12ch; margin: 8px 0 12px; font-size: clamp(34px,9vw,48px); line-height: 1.06; letter-spacing: -.055em; }
.admin-mobile-hero p { margin: 0; color: var(--color-ink-soft); line-height: 1.7; }
.admin-role-badge { display: inline-flex; width: fit-content; min-height: 42px; align-items: center; gap: 8px; padding: 0 14px; border: 1px solid #9fd5cb; border-radius: 999px; color: var(--color-primary); background: rgba(255,255,255,.72); font-weight: 800; }
.admin-mobile-metrics { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.admin-mobile-metrics article { display: grid; gap: 5px; min-height: 108px; align-content: center; padding: 18px; border: 1px solid var(--color-border); border-radius: 18px; background: #fff; box-shadow: var(--shadow-sm); }
.admin-mobile-metrics strong { color: var(--color-primary); font-size: 30px; }
.admin-mobile-metrics span { color: var(--color-muted); font-size: 13px; }
.admin-mobile-alert { margin: 0; padding: 14px; border-radius: 12px; color: var(--color-danger); background: var(--color-danger-soft); }
.admin-mobile-actions { display: grid; gap: 12px; }
.admin-mobile-actions a { display: grid; grid-template-columns: 52px 1fr 24px; gap: 14px; align-items: center; min-height: 94px; padding: 16px; border: 1px solid var(--color-border); border-radius: 18px; color: inherit; background: #fff; box-shadow: var(--shadow-sm); text-decoration: none; }
.admin-action-icon { display: grid; width: 52px; height: 52px; place-items: center; border-radius: 15px; color: var(--color-primary); background: var(--color-primary-soft); }
.admin-mobile-actions strong,.admin-mobile-actions small { display: block; }
.admin-mobile-actions strong { font-size: 17px; }
.admin-mobile-actions small { margin-top: 6px; color: var(--color-muted); line-height: 1.5; }
</style>
