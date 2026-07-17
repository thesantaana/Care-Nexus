<template>
  <section class="page profile-page">
    <div class="page-heading">
      <p class="eyebrow">MY ACCOUNT</p>
      <h1 tabindex="-1">我的账户</h1>
      <p>查看当前护理学习账户信息。</p>
    </div>

    <article class="profile-card">
      <div class="profile-identity">
        <img
          class="large-avatar"
          :src="sessionState.user?.avatarUrl || '/assets/default-avatar.png'"
          alt="当前用户头像"
          @error="$event.currentTarget.src = '/assets/default-avatar.png'"
        >
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
          <dt>身份</dt>
          <dd>{{ sessionState.user?.mainRoleName || '护工 / 护理人员' }}</dd>
        </div>
        <div>
          <dt>账号</dt>
          <dd>{{ sessionState.user?.username }}</dd>
        </div>
      </dl>
    </article>

    <button
      class="danger-button logout-button"
      type="button"
      :disabled="loggingOut"
      @click="logoutUser"
    >
      <AppIcon name="logout" />
      {{ loggingOut ? '正在退出' : '退出登录' }}
    </button>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { sessionState, signOut } from '../session.js'

const router = useRouter()
const loggingOut = ref(false)

async function logoutUser() {
  loggingOut.value = true
  try {
    await signOut()
  } finally {
    await router.replace({ name: 'login', query: { reason: 'logged-out' } })
    loggingOut.value = false
  }
}
</script>
