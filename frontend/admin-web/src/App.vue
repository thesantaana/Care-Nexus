<template>
  <RouterView v-if="isPublicLayout" />

  <div
    v-else
    class="app-shell"
  >
    <a
      class="skip-link"
      href="#main-content"
    >跳到主要内容</a>

    <button
      v-if="mobileNavigationOpen"
      class="sidebar-scrim"
      type="button"
      aria-label="关闭主导航"
      @click="mobileNavigationOpen = false"
    />

    <aside
      class="sidebar"
      :class="{ 'is-open': mobileNavigationOpen }"
    >
      <a
        class="brand"
        :href="portalHomeUrl"
        aria-label="返回 CareNexus 主页"
        title="返回主页"
      >
        <span
          class="brand-mark"
          aria-hidden="true"
        >
          <span />
          <span />
        </span>
        <span>
          <strong>CareNexus</strong>
          <small>颐联协作平台</small>
        </span>
      </a>

      <p class="nav-section-label">
        工作空间
      </p>
      <nav
        class="primary-navigation"
        aria-label="主导航"
      >
        <RouterLink
          v-for="item in visibleNavigation"
          :key="item.to"
          :to="item.to"
          @click="mobileNavigationOpen = false"
        >
          <AppIcon :name="item.icon" />
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-note">
        <AppIcon name="info" />
        <p>护理培训内容管理平台</p>
      </div>
    </aside>

    <div class="workspace">
      <header class="topbar">
        <div class="topbar-title">
          <button
            class="icon-button mobile-menu-button"
            type="button"
            aria-label="打开主导航"
            :aria-expanded="mobileNavigationOpen"
            @click="mobileNavigationOpen = true"
          >
            <AppIcon name="menu" />
          </button>
          <div>
            <p>CareNexus PC 端</p>
            <h1>{{ route.meta.title }}</h1>
          </div>
        </div>

        <div class="account-menu">
          <img
            class="account-avatar"
            :src="session.user?.avatarUrl || '/assets/default-avatar.png'"
            alt="当前用户头像"
            @error="$event.currentTarget.src = '/assets/default-avatar.png'"
          >
          <span class="account-copy">
            <strong>{{ session.user?.displayName || session.user?.username }}</strong>
            <small>{{ session.user?.mainRoleName }}</small>
          </span>
          <button
            class="button button-quiet logout-button"
            type="button"
            :disabled="session.loading"
            @click="handleLogout"
          >
            <AppIcon name="logout" />
            <span>{{ session.loading ? '退出中…' : '退出' }}</span>
          </button>
        </div>
      </header>

      <main
        id="main-content"
        ref="mainContent"
        class="content"
        tabindex="-1"
      >
        <RouterView v-slot="{ Component }">
          <Transition
            name="admin-page"
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
  </div>

  <div
    class="toast-region"
    aria-live="polite"
    aria-atomic="false"
  >
    <div
      v-for="message in feedback.messages"
      :key="message.id"
      class="toast"
      :class="`toast-${message.type}`"
      role="status"
    >
      <span>{{ message.message }}</span>
      <button
        type="button"
        aria-label="关闭消息"
        @click="dismissMessage(message.id)"
      >
        <AppIcon name="close" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppIcon from './components/AppIcon.vue'
import { navigationItems } from './config/navigation.js'
import { hasAnyPermission, hasRole, session, signOut } from './auth/session.js'
import { portalHomeUrl, redirectToPortal } from './auth/portal.js'
import { dismissMessage, feedback, notify } from './ui/feedback.js'

const route = useRoute()
const mobileNavigationOpen = ref(false)
const mainContent = ref(null)

const isPublicLayout = computed(() => route.meta.layout === 'auth')
const visibleNavigation = computed(() => navigationItems.filter((item) => (
  hasAnyPermission(item.permissions) && hasRole(item.roles)
)))
watch(() => route.fullPath, async () => {
  mobileNavigationOpen.value = false
  await nextTick()
  if (!isPublicLayout.value) {
    mainContent.value?.focus()
  }
})

async function handleLogout() {
  try {
    await signOut()
    redirectToPortal()
  } catch (error) {
    notify(error.message || '退出请求失败，本地登录状态已清理', 'warning')
    redirectToPortal()
  }
}
</script>
