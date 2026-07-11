export const navigationItems = [
  {
    label: '工作台',
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
  }
]
