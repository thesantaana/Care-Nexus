<template>
  <section class="login-page">
    <div
      class="login-brand"
      aria-hidden="true"
    >
      <span class="login-brand-symbol"><AppIcon name="pulse" /></span>
      <span>CareNexus</span>
    </div>

    <div class="login-intro">
      <p class="eyebrow">
        CARE CONNECTED
      </p>
      <h1 tabindex="-1">
        欢迎回到颐联
      </h1>
      <p>登录护理学习中心，继续今天的培训计划。</p>
    </div>

    <div class="login-card">
      <div
        v-if="notice"
        class="notice-banner"
        role="status"
      >
        <AppIcon name="info" />
        <span>{{ notice }}</span>
      </div>

      <form
        novalidate
        @submit.prevent="submitLogin"
      >
        <div class="field-group">
          <label for="username">账号</label>
          <input
            id="username"
            ref="usernameInput"
            v-model.trim="form.username"
            name="username"
            type="text"
            autocomplete="username"
            autocapitalize="none"
            spellcheck="false"
            required
            :aria-invalid="Boolean(fieldErrors.username)"
            :aria-describedby="fieldErrors.username ? 'username-error' : undefined"
          >
          <p
            v-if="fieldErrors.username"
            id="username-error"
            class="field-error"
          >
            {{ fieldErrors.username }}
          </p>
        </div>

        <div class="field-group">
          <label for="password">密码</label>
          <div class="password-field">
            <input
              id="password"
              ref="passwordInput"
              v-model="form.password"
              name="password"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="current-password"
              required
              :aria-invalid="Boolean(fieldErrors.password)"
              :aria-describedby="fieldErrors.password ? 'password-error' : undefined"
            >
            <button
              class="icon-button"
              type="button"
              :aria-label="showPassword ? '隐藏密码' : '显示密码'"
              :aria-pressed="showPassword"
              @click="showPassword = !showPassword"
            >
              <AppIcon :name="showPassword ? 'eye-off' : 'eye'" />
            </button>
          </div>
          <p
            v-if="fieldErrors.password"
            id="password-error"
            class="field-error"
          >
            {{ fieldErrors.password }}
          </p>
        </div>

        <p
          v-if="submitError"
          class="form-error"
          role="alert"
        >
          <AppIcon name="alert" />
          <span>{{ submitError }}</span>
        </p>

        <button
          class="primary-button login-button"
          type="submit"
          :disabled="submitting"
        >
          <span
            v-if="submitting"
            class="button-spinner"
            aria-hidden="true"
          />
          {{ submitting ? '正在验证…' : '登录并进入工作台' }}
        </button>
      </form>

      <p class="login-footnote">
        学习记录和培训进度会自动保存到当前账号。
      </p>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { signIn } from '../session.js'

const route = useRoute()
const router = useRouter()
const usernameInput = ref(null)
const passwordInput = ref(null)
const submitting = ref(false)
const showPassword = ref(false)
const submitError = ref('')
const form = reactive({ username: '', password: '' })
const fieldErrors = reactive({ username: '', password: '' })

const notice = computed(() => {
  const messages = {
    expired: '登录状态已失效，请重新登录。',
    'logged-out': '你已安全退出登录。',
    'local-logout': '本地登录已清除，但服务端暂时未响应。',
    unavailable: '暂时无法校验登录状态，请重新登录或稍后再试。'
  }
  return messages[route.query.reason] || ''
})

function validate() {
  fieldErrors.username = form.username ? '' : '请输入账号。'
  fieldErrors.password = form.password ? '' : '请输入密码。'
  return !fieldErrors.username && !fieldErrors.password
}

function safeRedirect() {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') && !redirect.startsWith('//')
    ? redirect
    : '/workspace'
}

async function submitLogin() {
  submitError.value = ''
  if (!validate()) {
    await nextTick()
    if (fieldErrors.username) {
      usernameInput.value?.focus()
    } else {
      passwordInput.value?.focus()
    }
    return
  }

  submitting.value = true
  try {
    await signIn({ username: form.username, password: form.password })
    form.password = ''
    await router.replace(safeRedirect())
  } catch (error) {
    submitError.value = error.message || '登录失败，请检查账号和密码。'
  } finally {
    submitting.value = false
  }
}

onMounted(() => usernameInput.value?.focus())
</script>
