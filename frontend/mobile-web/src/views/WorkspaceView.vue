<template>
  <section class="page page-workspace">
    <div class="workspace-hero">
      <div>
        <p class="eyebrow">
          {{ workspace.eyebrow }}
        </p>
        <h1 tabindex="-1">
          {{ greeting }}，{{ sessionState.user?.displayName }}
        </h1>
        <p>{{ workspace.description }}</p>
      </div>
      <span class="role-badge">
        <AppIcon :name="workspace.icon" />
        {{ sessionState.user?.mainRoleName }}
      </span>
    </div>

    <div
      class="connection-strip"
      role="status"
    >
      <span
        class="status-dot"
        aria-hidden="true"
      />
      <div>
        <strong>身份认证已连接</strong>
        <span>当前入口由真实角色与权限控制</span>
      </div>
    </div>

    <section aria-labelledby="available-title">
      <div class="section-heading">
        <div>
          <p class="section-kicker">
            AVAILABLE NOW
          </p>
          <h2 id="available-title">
            当前可用
          </h2>
        </div>
      </div>

      <div
        v-if="workspace.available.length"
        class="action-grid"
      >
        <RouterLink
          v-for="item in workspace.available"
          :key="item.path"
          class="action-card"
          :to="item.path"
        >
          <span class="action-icon"><AppIcon :name="item.icon" /></span>
          <span class="action-copy">
            <strong>{{ item.title }}</strong>
            <small>{{ item.description }}</small>
          </span>
          <AppIcon
            class="action-arrow"
            name="arrow-right"
          />
        </RouterLink>
      </div>

      <div
        v-else
        class="empty-card compact-empty"
      >
        <span class="empty-icon"><AppIcon name="info" /></span>
        <div>
          <h3>移动业务入口正在接入</h3>
          <p>{{ workspace.noAvailableMessage }}</p>
        </div>
      </div>
    </section>

    <section
      v-if="workspace.pending.length"
      aria-labelledby="pending-title"
    >
      <div class="section-heading">
        <div>
          <p class="section-kicker">
            IN PROGRESS
          </p>
          <h2 id="pending-title">
            后端尚未开放
          </h2>
        </div>
      </div>
      <div class="pending-list">
        <RouterLink
          v-for="item in workspace.pending"
          :key="item.path"
          class="pending-item"
          :to="item.path"
        >
          <span class="pending-icon"><AppIcon :name="item.icon" /></span>
          <span>
            <strong>{{ item.title }}</strong>
            <small>{{ item.description }}</small>
          </span>
          <span class="status-pill">待接入</span>
        </RouterLink>
      </div>
    </section>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import AppIcon from '../components/AppIcon.vue'
import { sessionState } from '../session.js'

const hour = new Date().getHours()
const greeting = hour < 11 ? '早上好' : hour < 14 ? '中午好' : hour < 18 ? '下午好' : '晚上好'

const workspaces = {
  CAREGIVER: {
    eyebrow: 'CAREGIVER WORKSPACE',
    icon: 'book',
    description: '从已发布培训资源开始学习，并随时查看真实学习进度。',
    available: [
      { title: '培训资源', description: '浏览文章、视频与 PPT 资料', path: '/training', icon: 'book' },
      { title: '学习进度', description: '查看累计时长与资源完成度', path: '/learning', icon: 'progress' }
    ],
    pending: [
      { title: '护理订单', description: '等待订单查询与服务执行接口', path: '/orders', icon: 'orders' }
    ]
  },
  ELDER: {
    eyebrow: 'ELDER WORKSPACE',
    icon: 'services',
    description: '移动服务入口会在护理预约与订单后端完成后开放。',
    available: [],
    noAvailableMessage: '服务浏览、地址、预约和订单接口尚未实现。',
    pending: [
      { title: '护理服务', description: '等待服务与预约接口', path: '/services', icon: 'services' }
    ]
  },
  FAMILY: {
    eyebrow: 'FAMILY WORKSPACE',
    icon: 'services',
    description: '老人绑定与代预约能力将在对应后端完成后开放。',
    available: [],
    noAvailableMessage: '老人绑定、服务浏览、预约和订单接口尚未实现。',
    pending: [
      { title: '老人护理服务', description: '等待老人绑定与服务接口', path: '/services', icon: 'services' }
    ]
  },
  DOCTOR: {
    eyebrow: 'DOCTOR WORKSPACE',
    icon: 'heart',
    description: '医生健康管理后端完成前，仅保留真实登录与身份校验。',
    available: [],
    noAvailableMessage: '授权老人、健康记录、预警和随访接口尚未实现。',
    pending: [
      { title: '健康管理', description: '等待医生健康管理后端', path: '/doctor', icon: 'heart' }
    ]
  },
  HEALTH_MANAGER: {
    eyebrow: 'HEALTH WORKSPACE',
    icon: 'heart',
    description: '健康管理与医生老人授权接口尚在后续任务中。',
    available: [],
    noAvailableMessage: '健康管理业务接口尚未实现。',
    pending: [
      { title: '健康管理', description: '等待健康档案与授权接口', path: '/doctor', icon: 'heart' }
    ]
  }
}

const managementWorkspace = {
  eyebrow: 'MANAGEMENT ACCOUNT',
  icon: 'user',
  description: '该账号已完成移动端认证，业务管理请使用 PC 工作台。',
  available: [],
  noAvailableMessage: '移动端不提供未完成的管理业务页面，也不会展示模拟管理数据。',
  pending: []
}

const workspace = computed(() => workspaces[sessionState.user?.mainRoleCode] || managementWorkspace)
</script>
