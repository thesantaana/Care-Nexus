import { createRouter, createWebHistory } from 'vue-router'
import ForbiddenView from '../views/ForbiddenView.vue'
import LoginView from '../views/LoginView.vue'
import NotFoundView from '../views/NotFoundView.vue'
import ProfileView from '../views/ProfileView.vue'
import WorkspaceView from '../views/WorkspaceView.vue'
import { ensureSession, hasPermission, sessionState } from '../session.js'

const routes = [
  { path: '/', redirect: '/workspace' },
  {
    path: '/notes',
    name: 'notes',
    component: () => import('../views/NotesView.vue'),
    meta: { requiresAuth: true, roles: ['CAREGIVER'], title: '学习笔记' }
  },
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
    path: '/training/:resourceId(\\d+)/:section(discussions|assignments|exam|mistakes|records)',
    name: 'course-feature',
    component: () => import('../views/CourseFeatureView.vue'),
    props: true,
    meta: {
      requiresAuth: true,
      roles: ['CAREGIVER'],
      permissions: ['training:resource:view'],
      title: '课程学习'
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
    path: '/admin/:section(resources|assignments|exams|ai-drafts|scores)',
    name: 'admin-section',
    component: () => import('../views/AdminSectionView.vue'),
    props: true,
    meta: {
      requiresAuth: true,
      roles: ['ADMIN'],
      permissions: ['training:resource:manage'],
      title: '移动管理'
    }
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
