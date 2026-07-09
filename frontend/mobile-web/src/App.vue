<template>
  <div class="mobile-shell">
    <header>
      <strong>CareNexus</strong>
      <span>移动端</span>
    </header>
    <main>
      <RouterView />
    </main>
    <nav>
      <RouterLink
        v-for="item in menu"
        :key="item.path"
        :to="item.path"
      >
        {{ item.label }}
      </RouterLink>
    </nav>
  </div>
</template>

<script setup>
const menu = [
  { label: '登录', path: '/login' },
  { label: '服务', path: '/services' },
  { label: '预约', path: '/service-detail' },
  { label: '订单', path: '/orders' },
  { label: '学习', path: '/training' }
]
</script>
