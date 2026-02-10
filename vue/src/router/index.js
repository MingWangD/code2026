import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/manager',
      component: () => import('/src/views/Manager.vue'),
      redirect: '/manager/dashboard',
      children: [
        {
          path: 'dashboard',
          component: () => import('/src/views/Dashboard.vue'),
          meta: { title: '学情预警看板' }
        },
        {
          path: 'home',
          component: () => import('/src/views/manager/Home.vue'),
          meta: { title: '统计分析' }
        },
        {
          path: 'admin',
          component: () => import('/src/views/manager/Admin.vue'),
          meta: { title: '管理员信息' }
        },
      ]
    },

    {
      path: '/student',
      component: () => import('/src/views/Student.vue'),
      meta: { title: '学生端演示' }
    },

    {
      path: '/login',
      component: () => import('/src/views/Login.vue'),
      meta: { title: '登录' }
    }
  ]
})

export default router