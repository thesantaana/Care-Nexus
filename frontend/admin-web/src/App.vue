<template>
  <div class="app-shell">
    <aside class="sidebar">
      <h1>CareNexus</h1>
      <p>PC端工作台</p>
      <nav>
        <RouterLink
          v-for="item in menu"
          :key="item.path"
          :to="item.path"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
const menu = [
  { label: '首页', path: '/' },
  { label: '登录', path: '/login' },
  { label: '护理培训', path: '/training' },
  { label: '资源详情', path: '/training/resource' },
  { label: '医生服务', path: '/doctor' },
  { label: '综合管理', path: '/admin' }
]
</script>
