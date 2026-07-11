<template>
  <section class="page centered-page">
    <div class="empty-card access-card">
      <span class="empty-icon danger-icon"><AppIcon name="alert" /></span>
      <p class="eyebrow">
        ACCESS RESTRICTED
      </p>
      <h1 tabindex="-1">
        当前身份不能访问
      </h1>
      <p>该入口与你的主要角色或功能权限不匹配。</p>
      <RouterLink
        class="primary-button"
        to="/workspace"
      >
        返回我的工作台
      </RouterLink>
    </div>
  </section>
</template>

<script setup>
import AppIcon from '../components/AppIcon.vue'
</script>
