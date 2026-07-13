<template>
  <a
    class="skip-link"
    href="#main-content"
  >跳到主要内容</a>
  <div
    class="mobile-shell"
    :class="{ 'public-shell': isPublicRoute }"
  >
    <aside
      v-if="!isPublicRoute"
      class="learning-sidebar"
    >
      <RouterLink
        class="brand-link"
        to="/workspace"
        aria-label="CareNexus 护工培训"
      >
        <span class="brand-symbol"><AppIcon name="pulse" /></span>
        <span>
          <strong>CareNexus</strong>
          <small>护理学习平台</small>
        </span>
      </RouterLink>
      <RouterLink
        class="sidebar-profile"
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

      <nav
        class="sidebar-nav"
        aria-label="学习平台导航"
      >
        <RouterLink
          v-for="item in navigationItems"
          :key="item.path"
          :to="item.path"
        >
          <AppIcon :name="item.icon" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>
      <div class="sidebar-footer">
        <span>CareNexus</span><small>持续学习，专业照护</small>
      </div>
    </aside>

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
  const items = [{ label: '工作台', path: '/workspace', icon: 'home' }]
  items.push(
    { label: '培训课程', path: '/training', icon: 'book' },
    { label: '学习进度', path: '/learning', icon: 'progress' },
    { label: '学习笔记', path: '/notes', icon: 'note' }
  )
  items.push({ label: '我的账号', path: '/profile', icon: 'user' })
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
