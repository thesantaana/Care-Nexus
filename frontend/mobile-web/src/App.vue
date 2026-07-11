<template>
  <a
    class="skip-link"
    href="#main-content"
  >跳到主要内容</a>
  <div
    class="mobile-shell"
    :class="{ 'public-shell': isPublicRoute }"
  >
    <header
      v-if="!isPublicRoute"
      class="app-header"
    >
      <RouterLink
        class="brand-link"
        to="/workspace"
        aria-label="CareNexus 移动工作台"
      >
        <span class="brand-symbol"><AppIcon name="pulse" /></span>
        <span>
          <strong>CareNexus</strong>
          <small>颐联移动工作台</small>
        </span>
      </RouterLink>
      <RouterLink
        class="user-chip"
        to="/profile"
        aria-label="查看我的账号"
      >
        <span
          class="user-avatar"
          aria-hidden="true"
        >{{ userInitial }}</span>
        <span class="user-chip-copy">
          <strong>{{ sessionState.user?.displayName || '当前用户' }}</strong>
          <small>{{ sessionState.user?.mainRoleName || '身份校验中' }}</small>
        </span>
      </RouterLink>
    </header>

    <main
      id="main-content"
      ref="mainContent"
      :aria-busy="sessionState.checking"
    >
      <RouterView v-slot="{ Component }">
        <Transition
          name="page-fade"
          mode="out-in"
        >
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>

    <nav
      v-if="!isPublicRoute"
      class="bottom-nav"
      aria-label="主要导航"
    >
      <RouterLink
        v-for="item in navigationItems"
        :key="item.path"
        :to="item.path"
        :aria-label="item.label"
      >
        <AppIcon :name="item.icon" />
        <span>{{ item.label }}</span>
      </RouterLink>
    </nav>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppIcon from './components/AppIcon.vue'
import { sessionState } from './session.js'

const route = useRoute()
const mainContent = ref(null)

const isPublicRoute = computed(() => Boolean(route.meta.public))
const userInitial = computed(() => {
  const name = sessionState.user?.displayName || sessionState.user?.username || 'C'
  return name.trim().slice(0, 1).toUpperCase()
})

const navigationItems = computed(() => {
  const role = sessionState.user?.mainRoleCode
  const items = [{ label: '工作台', path: '/workspace', icon: 'home' }]

  if (role === 'CAREGIVER') {
    items.push(
      { label: '学习', path: '/training', icon: 'book' },
      { label: '进度', path: '/learning', icon: 'progress' },
      { label: '订单', path: '/orders', icon: 'orders' }
    )
  } else if (role === 'ELDER' || role === 'FAMILY') {
    items.push(
      { label: '服务', path: '/services', icon: 'services' },
      { label: '订单', path: '/orders', icon: 'orders' }
    )
  } else if (role === 'DOCTOR' || role === 'HEALTH_MANAGER') {
    items.push({ label: '健康', path: '/doctor', icon: 'heart' })
  }

  items.push({ label: '我的', path: '/profile', icon: 'user' })
  return items.slice(0, 5)
})

watch(
  () => route.fullPath,
  async () => {
    await nextTick()
    const heading = mainContent.value?.querySelector('h1')
    heading?.focus({ preventScroll: true })
  },
  { immediate: true }
)
</script>
