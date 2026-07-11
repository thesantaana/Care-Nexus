<template>
  <section class="page page-wide">
    <div class="hero-card dashboard-hero">
      <div>
        <p class="eyebrow">
          角色工作台
        </p>
        <h2>{{ greeting }}，{{ session.user?.displayName || session.user?.username }}</h2>
        <p>当前以“{{ session.user?.mainRoleName }}”身份登录。下面只展示账号实际拥有的工作入口。</p>
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
      <p>{{ session.user?.permissionCodes?.length || 0 }} 项服务端权限已同步</p>
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
          <span
            class="availability"
            :class="card.available ? 'is-ready' : 'is-pending'"
          >{{ card.available ? '已接入' : '等待后端' }}</span>
          <h3>{{ card.title }}</h3>
          <p>{{ card.description }}</p>
        </div>
        <RouterLink
          class="text-link"
          :to="card.to"
        >
          {{ card.available ? '进入模块' : '查看范围' }}
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

    <section
      class="info-panel"
      aria-labelledby="permission-title"
    >
      <div>
        <p class="eyebrow">
          权限同步
        </p>
        <h2 id="permission-title">
          当前权限代码
        </h2>
      </div>
      <div class="permission-list">
        <code
          v-for="permission in session.user?.permissionCodes || []"
          :key="permission"
        >{{ permission }}</code>
        <span v-if="!session.user?.permissionCodes?.length">暂无权限代码</span>
      </div>
    </section>
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
    description: '管理或查看培训分类、标签和资源，数据来自当前后端。',
    to: '/training/resources',
    icon: 'book',
    permissions: ['training:resource:view', 'training:resource:manage'],
    available: true
  },
  {
    title: '护理订单',
    description: '护理预约、分配与服务执行将在订单后端完成后接入。',
    to: '/care',
    icon: 'care',
    permissions: ['care:order:view', 'care:order:assign'],
    available: false
  },
  {
    title: '医生服务',
    description: '健康档案、预警与随访将在医生健康管理后端完成后接入。',
    to: '/doctor',
    icon: 'doctor',
    permissions: ['doctor:elder:view', 'doctor:elder:authorize'],
    available: false
  },
  {
    title: '综合管理',
    description: '用户、角色、字典与日志管理将在对应后端接口完成后接入。',
    to: '/admin',
    icon: 'shield',
    permissions: ['system:user:view', 'system:user:manage', 'system:role:view'],
    available: false
  }
]

const workspaceCards = computed(() => cards.filter((card) => hasAnyPermission(card.permissions)))
</script>
