import { h } from 'vue';

function block(title, lines) {
  return h('article', { class: 'mobile-card' }, [
    h('h3', title),
    h('ul', lines.map((line) => h('li', line)))
  ]);
}

function page(title, description, blocks) {
  return {
    render() {
      return h('section', { class: 'page' }, [
        h('h2', title),
        h('p', description),
        h('div', { class: 'stack' }, blocks)
      ]);
    }
  };
}

export const routes = [
  {
    path: '/',
    redirect: '/services'
  },
  {
    path: '/login',
    component: page('移动端登录', '老人、家属和护工通过统一账号进入对应移动功能。', [
      block('账号规则', ['一个账号一个主要业务角色', '停用账号不得继续操作'])
    ])
  },
  {
    path: '/services',
    component: page('移动端服务首页', '老人和家属浏览服务、维护地址并提交护理预约。', [
      block('服务分类', ['基础护理', '康复护理', '陪诊协助']),
      block('快捷入口', ['地址管理', '我的订单', '投诉处理状态'])
    ])
  },
  {
    path: '/service-detail',
    component: page('服务详情与预约页面', '展示服务说明、老人、地址和预约时间选择结构。', [
      block('服务信息', ['服务名称', '服务说明', '注意事项']),
      block('预约信息', ['选择老人', '选择地址', '预约时间', '提交后进入待分配'])
    ])
  },
  {
    path: '/orders',
    component: page('护工订单执行页面', '护工只能查看分配给自己的订单，并按状态执行服务。', [
      block('订单列表', ['待确认', '已确认', '服务中']),
      block('执行操作', ['确认服务', '开始服务', '完成服务并填写记录'])
    ])
  },
  {
    path: '/training',
    component: page('移动培训学习', '护工可在移动端浏览培训资源、查看学习记录和参加考核。', [
      block('学习入口', ['文章', '视频', 'PPT']),
      block('考核状态', ['整体学习时长', '最近学习时间', '通过状态'])
    ])
  }
];
