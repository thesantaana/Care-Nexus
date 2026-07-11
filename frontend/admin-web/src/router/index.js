import { createRouter, createWebHistory } from 'vue-router'
import { hasAnyPermission, hasRole, isAuthenticated } from '../auth/session.js'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { title: '登录', layout: 'auth', public: true }
  },
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { title: '角色工作台' }
  },
  {
    path: '/training',
    redirect: '/training/resources'
  },
  {
    path: '/training/resources',
    name: 'training-resources',
    component: () => import('../views/training/TrainingResourcesView.vue'),
    meta: {
      title: '培训资源',
      permissions: ['training:resource:view', 'training:resource:manage']
    }
  },
  {
    path: '/training/resources/new',
    name: 'training-resource-new',
    component: () => import('../views/training/TrainingResourceFormView.vue'),
    meta: { title: '新增培训资源', permissions: ['training:resource:manage'] }
  },
  {
    path: '/training/resources/:id',
    name: 'training-resource-detail',
    component: () => import('../views/training/TrainingResourceDetailView.vue'),
    meta: {
      title: '培训资源详情',
      permissions: ['training:resource:view', 'training:resource:manage']
    }
  },
  {
    path: '/training/resources/:id/edit',
    name: 'training-resource-edit',
    component: () => import('../views/training/TrainingResourceFormView.vue'),
    meta: { title: '编辑培训资源', permissions: ['training:resource:manage'] }
  },
  {
    path: '/training/catalogs',
    name: 'training-catalogs',
    component: () => import('../views/training/TrainingCatalogView.vue'),
    meta: { title: '分类与标签', permissions: ['training:resource:manage'] }
  },
  {
    path: '/care',
    name: 'care',
    component: () => import('../views/UnavailableModuleView.vue'),
    props: {
      eyebrow: 'CARE SERVICE',
      title: '护理订单待接入',
      description: '护理预约、人工分配和护工执行接口尚未在后端实现，因此此处不展示模拟订单。',
      capabilities: ['预约与地址', '人工分配', '服务执行与评价']
    },
    meta: { title: '护理订单', permissions: ['care:order:view', 'care:order:assign'] }
  },
  {
    path: '/doctor',
    name: 'doctor',
    component: () => import('../views/UnavailableModuleView.vue'),
    props: {
      eyebrow: 'HEALTH MANAGEMENT',
      title: '医生健康管理待接入',
      description: '授权老人、健康记录、预警和随访接口尚未在后端实现，因此此处不展示模拟健康数据。',
      capabilities: ['授权老人档案', '健康预警', '随访与健康评估']
    },
    meta: { title: '医生服务', permissions: ['doctor:elder:view', 'doctor:elder:authorize'] }
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../views/UnavailableModuleView.vue'),
    props: {
      eyebrow: 'SYSTEM GOVERNANCE',
      title: '综合管理待接入',
      description: '用户、角色、权限、字典和日志管理接口尚未在后端实现，因此此处不展示模拟管理数据。',
      capabilities: ['用户与角色', '服务项目与字典', '操作日志']
    },
    meta: {
      title: '综合管理',
      permissions: ['system:user:view', 'system:user:manage', 'system:role:view']
    }
  },
  {
    path: '/forbidden',
    name: 'forbidden',
    component: () => import('../views/ForbiddenView.vue'),
    meta: { title: '无权访问' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/NotFoundView.vue'),
    meta: { title: '页面不存在', public: true, layout: 'auth' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

router.beforeEach((to) => {
  if (to.name === 'login' && isAuthenticated.value) {
    return { name: 'dashboard' }
  }
  if (!to.meta.public && !isAuthenticated.value) {
    return {
      name: 'login',
      query: { redirect: to.fullPath }
    }
  }
  if (to.meta.permissions && !hasAnyPermission(to.meta.permissions)) {
    return { name: 'forbidden', query: { from: to.fullPath } }
  }
  if (to.meta.roles && !hasRole(to.meta.roles)) {
    return { name: 'forbidden', query: { from: to.fullPath } }
  }
  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '工作台'} · CareNexus`
})

export default router
