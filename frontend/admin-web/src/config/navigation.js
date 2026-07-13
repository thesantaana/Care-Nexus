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
  },
  {
    label: '题库与考核',
    to: '/training/exams',
    icon: 'exam',
    permissions: ['training:resource:manage']
  },
  {
    label: 'AI草稿审核',
    to: '/training/ai-drafts',
    icon: 'spark',
    permissions: ['training:resource:manage']
  },
  {
    label: '培训成绩',
    to: '/training/scores',
    icon: 'chart',
    permissions: ['training:resource:manage']
  }
]
