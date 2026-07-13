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
    meta: { title: '管理工作台' }
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
    path: '/training/exams',
    name: 'training-exams',
    component: () => import('../views/training/TrainingExamsView.vue'),
    meta: { title: '题库与考核', permissions: ['training:resource:manage'] }
  },
  {
    path: '/training/ai-drafts',
    name: 'training-ai-drafts',
    component: () => import('../views/training/TrainingAiDraftsView.vue'),
    meta: { title: 'AI草稿审核', permissions: ['training:resource:manage'] }
  },
  {
    path: '/training/scores',
    name: 'training-scores',
    component: () => import('../views/training/TrainingScoresView.vue'),
    meta: { title: '培训成绩', permissions: ['training:resource:manage'] }
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
