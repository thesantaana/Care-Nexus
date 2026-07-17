<template>
  <AdminMobileDashboard v-if="isAdmin" />
  <section v-else class="page page-workspace">
    <div class="workspace-hero">
      <div>
        <p class="eyebrow">
          护理学习中心
        </p>
        <h1 tabindex="-1">
          {{ greeting }}，{{ sessionState.user?.displayName }}
        </h1>
        <p>选择培训资料开始学习，持续积累学习时长并完成护理知识考核。</p>
      </div>
      <span class="role-badge">
        <AppIcon name="book" />
        护工
      </span>
    </div>

    <section aria-labelledby="learning-title">
      <div class="section-heading">
        <div>
          <p class="section-kicker">
            LEARNING
          </p>
          <h2 id="learning-title">
            开始今天的学习
          </h2>
        </div>
      </div>

      <div class="action-grid">
        <RouterLink
          class="action-card"
          to="/training"
        >
          <span class="action-icon"><AppIcon name="book" /></span>
          <span class="action-copy">
            <strong>培训资源</strong>
            <small>浏览文章、视频与PPT学习资料</small>
          </span>
          <AppIcon
            class="action-arrow"
            name="arrow-right"
          />
        </RouterLink>
        <RouterLink
          class="action-card"
          to="/learning"
        >
          <span class="action-icon"><AppIcon name="progress" /></span>
          <span class="action-copy">
            <strong>学习进度</strong>
            <small>查看累计学习时长与培训状态</small>
          </span>
          <AppIcon
            class="action-arrow"
            name="arrow-right"
          />
        </RouterLink>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import AdminMobileDashboard from '../components/AdminMobileDashboard.vue'
import AppIcon from '../components/AppIcon.vue'
import { sessionState } from '../session.js'

const isAdmin = computed(() => sessionState.user?.mainRoleCode === 'ADMIN')
const hour = new Date().getHours()
const greeting = hour < 11 ? '早上好' : hour < 14 ? '中午好' : hour < 18 ? '下午好' : '晚上好'
</script>
