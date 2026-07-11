<template>
  <section class="page profile-page">
    <div class="page-heading">
      <p class="eyebrow">
        MY ACCOUNT
      </p>
      <h1 tabindex="-1">
        我的账号
      </h1>
      <p>查看当前账号、角色和学习权限。</p>
    </div>

    <article class="profile-card">
      <div class="profile-identity">
        <span
          class="large-avatar"
          aria-hidden="true"
        >{{ initial }}</span>
        <div>
          <h2>{{ sessionState.user?.displayName }}</h2>
          <p>@{{ sessionState.user?.username }}</p>
        </div>
        <span class="account-status">
          <AppIcon name="check" />
          {{ sessionState.user?.accountStatus === 'NORMAL' ? '正常' : sessionState.user?.accountStatus }}
        </span>
      </div>

      <dl class="detail-list">
        <div>
          <dt>主要角色</dt>
          <dd>{{ sessionState.user?.mainRoleName }}</dd>
        </div>
        <div>
          <dt>角色代码</dt>
          <dd>{{ sessionState.user?.mainRoleCode }}</dd>
        </div>
        <div>
          <dt>账号 ID</dt>
          <dd>{{ sessionState.user?.userId }}</dd>
        </div>
      </dl>
    </article>

    <section
      class="permission-card"
      aria-labelledby="permission-title"
    >
      <div class="section-heading inline-heading">
        <div>
          <p class="section-kicker">
            RBAC
          </p>
          <h2 id="permission-title">
            当前权限
          </h2>
        </div>
        <button
          class="secondary-button compact-button"
          type="button"
          :disabled="refreshing"
          @click="refresh"
        >
          <AppIcon name="refresh" />
          {{ refreshing ? '更新中' : '更新' }}
        </button>
      </div>
      <ul
        v-if="permissions.length"
        class="permission-list"
      >
        <li
          v-for="permission in permissions"
          :key="permission"
        >
          {{ permission }}
        </li>
      </ul>
      <p
        v-else
        class="muted-copy"
      >
        当前账号未返回可展示的功能权限码。
      </p>
      <p
        v-if="refreshMessage"
        class="inline-feedback"
        :class="{ error: refreshFailed }"
        role="status"
      >
        {{ refreshMessage }}
      </p>
    </section>

    <button
      class="danger-button logout-button"
      type="button"
      :disabled="loggingOut"
      @click="logoutUser"
    >
      <AppIcon name="logout" />
      {{ loggingOut ? '正在退出…' : '退出登录' }}
    </button>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { refreshCurrentUser, sessionState, signOut } from '../session.js'

const router = useRouter()
const refreshing = ref(false)
const loggingOut = ref(false)
const refreshMessage = ref('')
const refreshFailed = ref(false)

const initial = computed(() => (sessionState.user?.displayName || sessionState.user?.username || 'C').slice(0, 1))
const permissions = computed(() => sessionState.user?.permissionCodes || [])

async function refresh() {
  refreshing.value = true
  refreshMessage.value = ''
  refreshFailed.value = false
  try {
    const user = await refreshCurrentUser()
    if (!user) throw new Error('当前用户信息更新失败。')
    refreshMessage.value = '账号与权限信息已更新。'
  } catch (error) {
    refreshFailed.value = true
    refreshMessage.value = error.message || '更新失败，请稍后重试。'
  } finally {
    refreshing.value = false
  }
}

async function logoutUser() {
  loggingOut.value = true
  try {
    await signOut()
    await router.replace({ name: 'login', query: { reason: 'logged-out' } })
  } catch {
    await router.replace({ name: 'login', query: { reason: 'local-logout' } })
  } finally {
    loggingOut.value = false
  }
}
</script>
