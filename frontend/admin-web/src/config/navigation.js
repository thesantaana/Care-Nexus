export const navigationItems = [
  {
    label: '角色工作台',
    to: '/',
    icon: 'home'
  },
  {
    label: '培训资源',
    to: '/training/resources',
    icon: 'book',
    permissions: ['training:resource:view', 'training:resource:manage']
  },
  {
    label: '分类与标签',
    to: '/training/catalogs',
    icon: 'tag',
    permissions: ['training:resource:manage']
  },
  {
    label: '护理订单',
    to: '/care',
    icon: 'care',
    permissions: ['care:order:view', 'care:order:assign']
  },
  {
    label: '医生服务',
    to: '/doctor',
    icon: 'doctor',
    permissions: ['doctor:elder:view', 'doctor:elder:authorize']
  },
  {
    label: '综合管理',
    to: '/admin',
    icon: 'shield',
    permissions: ['system:user:view', 'system:user:manage', 'system:role:view']
  }
]
