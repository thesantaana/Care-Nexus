<template>
  <section class="page page-wide admin-module">
    <header class="admin-welcome">
      <div>
        <p class="eyebrow">
          管理概览
        </p><h2>{{ greeting }}，{{ session.user?.displayName || session.user?.username }}</h2><p>集中管理护理培训课程、考核、AI题目草稿和护工培训结果。</p>
      </div><span class="admin-role">{{ session.user?.mainRoleName }}</span>
    </header>
    <div class="admin-metrics admin-metrics-four">
      <article><strong>{{ stats.resources }}</strong><span>培训课程</span></article><article><strong>{{ stats.exams }}</strong><span>课程考核</span></article><article><strong>{{ stats.pendingDrafts }}</strong><span>待审核AI草稿</span></article><article><strong>{{ stats.passedCaregivers }}</strong><span>已通过培训护工</span></article>
    </div>
    <div
      v-if="error"
      class="admin-alert"
    >
      {{ error }}
    </div>
    <div class="admin-dashboard-grid">
      <RouterLink
        v-for="card in cards"
        :key="card.to"
        class="admin-module-link"
        :to="card.to"
      >
        <AppIcon :name="card.icon" /><div><h3>{{ card.title }}</h3><p>{{ card.description }}</p></div><AppIcon name="chevron" />
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { session } from '../auth/session.js'
import { listResources } from '../api/training.js'
import { listCaregiverScores, listDrafts, listExams } from '../api/adminTraining.js'
const hour = new Date().getHours(); const greeting = hour < 12 ? '早上好' : hour < 18 ? '下午好' : '晚上好'
const stats = reactive({ resources: 0, exams: 0, pendingDrafts: 0, passedCaregivers: 0 }); const error = ref('')
const cards = [
  { title: '课程管理', description: '维护课程内容、封面、标签和发布状态。', to: '/training/resources', icon: 'book' },
  { title: '发布作业', description: '上传 DOCX 作业，确认识别出的题目后发布到指定课程。', to: '/training/assignments/publish', icon: 'upload' },
  { title: '题库与考核', description: '按课程维护独立考核和60分及格规则。', to: '/training/exams', icon: 'exam' },
  { title: 'AI草稿审核', description: '审核基于培训资料生成的题目草稿。', to: '/training/ai-drafts', icon: 'spark' },
  { title: '培训成绩', description: '查看护工每课最高分、平均分和通过状态。', to: '/training/scores', icon: 'chart' }
]
onMounted(async () => { try { const [resources, exams, drafts, scores] = await Promise.all([listResources({ pageNo: 1, pageSize: 1 }), listExams(), listDrafts({ draftStatus: 'DRAFT', pageNo: 1, pageSize: 1 }), listCaregiverScores()]); stats.resources = resources?.total || 0; stats.exams = exams?.length || 0; stats.pendingDrafts = drafts?.total || 0; stats.passedCaregivers = scores?.filter((item) => item.trainingPassed).length || 0 } catch (e) { error.value = e.message } })
</script>
