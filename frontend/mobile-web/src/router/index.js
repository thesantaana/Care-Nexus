import { createRouter, createWebHistory } from 'vue-router'
import ForbiddenView from '../views/ForbiddenView.vue'
import LoginView from '../views/LoginView.vue'
import NotFoundView from '../views/NotFoundView.vue'
import ProfileView from '../views/ProfileView.vue'
import UnavailableView from '../views/UnavailableView.vue'
import WorkspaceView from '../views/WorkspaceView.vue'
import { ensureSession, hasPermission, sessionState } from '../session.js'

const routes = [
  { path: '/', redirect: '/workspace' },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { public: true, title: '登录' }
  },
  {
    path: '/workspace',
    name: 'workspace',
    component: WorkspaceView,
    meta: { requiresAuth: true, title: '移动工作台' }
  },
  {
    path: '/training',
    name: 'training',
    component: () => import('../views/TrainingListView.vue'),
    meta: {
      requiresAuth: true,
      roles: ['CAREGIVER'],
      permissions: ['training:resource:view'],
      title: '培训资源'
    }
  },
  {
    path: '/training/:resourceId(\\d+)',
    name: 'training-detail',
    component: () => import('../views/TrainingDetailView.vue'),
    props: true,
    meta: {
      requiresAuth: true,
      roles: ['CAREGIVER'],
      permissions: ['training:resource:view'],
      title: '资源详情'
    }
  },
  {
    path: '/learning',
    name: 'learning',
    component: () => import('../views/LearningProgressView.vue'),
    meta: {
      requiresAuth: true,
      roles: ['CAREGIVER'],
      permissions: ['training:resource:view'],
      title: '学习进度'
    }
  },
  {
    path: '/services',
    name: 'services',
    component: UnavailableView,
    props: {
      title: '老人 / 家属护理服务',
      description: '服务浏览、老人绑定、地址和预约能力属于后续护理订单任务。',
      detail: '当前后端尚未提供移动服务、老人绑定、地址或预约接口。',
      icon: 'services'
    },
    meta: { requiresAuth: true, roles: ['ELDER', 'FAMILY'], title: '护理服务' }
  },
  {
    path: '/orders',
    name: 'orders',
    component: UnavailableView,
    props: {
      title: '护理订单待接入',
      description: '派单、确认服务、开始服务和完成服务将在护理订单后端完成后开放。',
      detail: '当前后端尚未提供护工订单查询或状态变更接口，页面不会展示模拟订单。',
      icon: 'orders'
    },
    meta: {
      requiresAuth: true,
      roles: ['CAREGIVER'],
      permissions: ['care:order:view'],
      title: '护理订单'
    }
  },
  {
    path: '/doctor',
    name: 'doctor',
    component: UnavailableView,
    props: {
      title: '医生健康管理',
      description: '健康档案、记录、预警、随访和评估将在医生后端任务完成后开放。',
      detail: '当前后端尚未提供医生健康管理业务接口。',
      icon: 'heart'
    },
    meta: { requiresAuth: true, roles: ['DOCTOR', 'HEALTH_MANAGER'], title: '健康管理' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true, title: '我的账号' }
  },
  {
    path: '/forbidden',
    name: 'forbidden',
    component: ForbiddenView,
    meta: { requiresAuth: true, title: '无访问权限' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: { requiresAuth: true, title: '页面不存在' }
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.path === from.path) return undefined
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  if (to.meta.public) {
    if (!sessionState.token) return true
    const user = await ensureSession()
    return user ? { name: 'workspace' } : true
  }

  if (!to.meta.requiresAuth) return true
  if (!sessionState.token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  const user = await ensureSession()
  if (!user) {
    return {
      name: 'login',
      query: {
        reason: sessionState.token ? 'unavailable' : 'expired',
        redirect: to.fullPath
      }
    }
  }

  const allowedRoles = to.meta.roles || []
  if (allowedRoles.length && !allowedRoles.includes(user.mainRoleCode)) {
    return { name: 'forbidden' }
  }

  const requiredPermissions = to.meta.permissions || []
  if (requiredPermissions.some((permission) => !hasPermission(permission))) {
    return { name: 'forbidden' }
  }

  return true
})

router.afterEach((to) => {
  document.title = `${to.meta.title || '移动工作台'} · CareNexus`
})
