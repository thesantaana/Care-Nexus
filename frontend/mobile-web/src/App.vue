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
        <img
          class="user-avatar"
          :src="sessionState.user?.avatarUrl || '/assets/default-avatar.png'"
          alt="当前用户头像"
          @error="$event.currentTarget.src = '/assets/default-avatar.png'"
        >
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

    <nav v-if="!isPublicRoute" class="mobile-bottom-nav" aria-label="移动端主导航">
      <RouterLink v-for="item in navigationItems" :key="`bottom-${item.path}`" :to="item.path">
        <AppIcon :name="item.icon" /><span>{{ item.label }}</span>
      </RouterLink>
    </nav>

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
          <component
            :is="Component"
            :key="route.fullPath"
          />
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

<style scoped>
.mobile-bottom-nav { display: none; }
@media (max-width: 760px) {
  .learning-sidebar { display: none; }
  .mobile-shell { display: block; }
  .mobile-shell:not(.public-shell) > main { width: 100%; margin: 0; padding-left: 0; }
  .mobile-bottom-nav {
    position: fixed; z-index: 80; right: 10px; bottom: 10px; left: 10px;
    display: grid; grid-template-columns: repeat(5, minmax(0, 1fr));
    min-height: 62px; border: 1px solid #d8e3e6; border-radius: 8px;
    background: rgba(255,255,255,.97); box-shadow: 0 12px 35px rgba(15,50,58,.16);
  }
  .mobile-bottom-nav a { display: grid; place-items: center; gap: 3px; padding: 8px 2px; color: #61747c; font-size: 11px; text-decoration: none; }
  .mobile-bottom-nav a :deep(.app-icon) { width: 21px; height: 21px; }
  .mobile-bottom-nav a.router-link-active { color: #087e75; font-weight: 700; }
}
</style>
