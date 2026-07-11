<template>
  <section class="page page-wide">
    <div class="hero-card dashboard-hero">
      <div>
        <p class="eyebrow">
          管理工作台
        </p>
        <h2>{{ greeting }}，{{ session.user?.displayName || session.user?.username }}</h2>
        <p>管理培训资料、内容分类和学习资源，为护工提供持续、规范的培训支持。</p>
      </div>
      <div
        class="identity-card"
        aria-label="当前身份信息"
      >
        <span>主角色</span>
        <strong>{{ session.user?.mainRoleName }}</strong>
        <small>{{ session.user?.mainRoleCode }}</small>
      </div>
    </div>

    <div class="section-heading">
      <div>
        <p class="eyebrow">
          可访问模块
        </p>
        <h2>从这里继续工作</h2>
      </div>
    </div>

    <div
      v-if="workspaceCards.length"
      class="workspace-grid"
    >
      <article
        v-for="card in workspaceCards"
        :key="card.to"
        class="workspace-card"
      >
        <div class="workspace-card-icon">
          <AppIcon :name="card.icon" />
        </div>
        <div class="workspace-card-copy">
          <h3>{{ card.title }}</h3>
          <p>{{ card.description }}</p>
        </div>
        <RouterLink
          class="text-link"
          :to="card.to"
        >
          进入模块
          <AppIcon name="chevron" />
        </RouterLink>
      </article>
    </div>

    <div
      v-else
      class="empty-state"
    >
      <AppIcon name="shield" />
      <h2>暂无可访问模块</h2>
      <p>账号已登录，但服务端尚未分配业务权限。请联系管理员检查角色配置。</p>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { hasAnyPermission, session } from '../auth/session.js'

const hour = new Date().getHours()
const greeting = hour < 12 ? '早上好' : hour < 18 ? '下午好' : '晚上好'

const cards = [
  {
    title: '护理培训',
    description: '维护培训分类、标签和文章、视频、PPT等学习资源。',
    to: '/training/resources',
    icon: 'book',
    permissions: ['training:resource:view', 'training:resource:manage']
  }
]

const workspaceCards = computed(() => cards.filter((card) => hasAnyPermission(card.permissions)))
</script>
