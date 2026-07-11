<template>
  <main class="auth-page">
    <section
      class="auth-brand-panel"
      aria-labelledby="brand-title"
    >
      <div class="auth-brand-content">
        <div class="brand brand-on-light">
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
        </div>
        <p class="eyebrow">
          智慧护理 · 可信协作
        </p>
        <h1 id="brand-title">
          让护理培训更清晰、更高效
        </h1>
        <p>集中管理培训资料、分类标签和考核内容，为护理人员提供统一学习入口。</p>
        <div
          class="auth-feature-list"
          aria-label="平台特性"
        >
          <span>培训内容管理</span>
          <span>学习权限控制</span>
          <span>考核与AI辅助</span>
        </div>
      </div>
      <div
        class="auth-orbit auth-orbit-one"
        aria-hidden="true"
      />
      <div
        class="auth-orbit auth-orbit-two"
        aria-hidden="true"
      />
    </section>

    <section
      class="auth-form-panel"
      aria-labelledby="login-title"
    >
      <div class="auth-form-card">
        <p class="eyebrow">
          欢迎回来
        </p>
        <h2 id="login-title">
          登录 PC 工作台
        </h2>
        <p class="auth-form-intro">
          使用管理员账号登录护理培训管理平台。
        </p>

        <div
          v-if="route.query.reason === 'expired'"
          class="alert alert-warning"
          role="status"
        >
          <AppIcon name="info" />
          <span>登录状态已失效，请重新登录。</span>
        </div>

        <form
          class="auth-form"
          novalidate
          @submit.prevent="submit"
        >
          <label class="field">
            <span>账号</span>
            <input
              ref="usernameInput"
              v-model.trim="form.username"
              name="username"
              type="text"
              autocomplete="username"
              placeholder="请输入账号"
              :aria-invalid="Boolean(errors.username)"
              :aria-describedby="errors.username ? 'username-error' : undefined"
            >
            <small
              v-if="errors.username"
              id="username-error"
              class="field-error"
              role="alert"
            >{{ errors.username }}</small>
          </label>

          <label class="field">
            <span>密码</span>
            <span class="password-field">
              <input
                v-model="form.password"
                name="password"
                :type="showPassword ? 'text' : 'password'"
                autocomplete="current-password"
                placeholder="请输入密码"
                :aria-invalid="Boolean(errors.password)"
                :aria-describedby="errors.password ? 'password-error' : undefined"
              >
              <button
                type="button"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                @click="showPassword = !showPassword"
              >
                {{ showPassword ? '隐藏' : '显示' }}
              </button>
            </span>
            <small
              v-if="errors.password"
              id="password-error"
              class="field-error"
              role="alert"
            >{{ errors.password }}</small>
          </label>

          <div
            v-if="requestError"
            class="alert alert-danger"
            role="alert"
          >
            <AppIcon name="info" />
            <span>{{ requestError }}</span>
          </div>

          <button
            class="button button-primary button-block"
            type="submit"
            :disabled="session.loading"
          >
            {{ session.loading ? '正在校验身份…' : '登录工作台' }}
          </button>
        </form>

        <p class="auth-security-note">
          CareNexus 使用加密认证保护账号安全。
        </p>
      </div>
    </section>
  </main>
</template>

<script setup>
import { nextTick, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppIcon from '../components/AppIcon.vue'
import { ApiError } from '../api/request.js'
import { session, signIn } from '../auth/session.js'

const route = useRoute()
const router = useRouter()
const usernameInput = ref(null)
const showPassword = ref(false)
const requestError = ref('')
const form = reactive({ username: '', password: '' })
const errors = reactive({ username: '', password: '' })

function validate() {
  errors.username = form.username ? '' : '请输入账号'
  errors.password = form.password ? '' : '请输入密码'
  return !errors.username && !errors.password
}

function loginErrorMessage(error) {
  if (error instanceof ApiError) {
    if (error.code === 'AUTH_INVALID_CREDENTIALS') {
      return '账号或密码不正确，请检查后重试。'
    }
    if (error.code === 'AUTH_ACCOUNT_DISABLED') {
      return '该账号已停用，请联系管理员。'
    }
  }
  return error.message || '登录失败，请稍后重试。'
}

function safeRedirect() {
  const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
  return redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/'
}

async function submit() {
  requestError.value = ''
  if (!validate()) {
    await nextTick()
    usernameInput.value?.focus()
    return
  }

  try {
    await signIn(form)
    await router.replace(safeRedirect())
  } catch (error) {
    requestError.value = loginErrorMessage(error)
  }
}
</script>
