import { createWebHistory, createRouter } from 'vue-router'
import Index from '../layout/Common/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/index',
      component: Index,
      meta: { title: '首页' },
      children: [
        {
          path: '/index',
          name: 'index',
          component: () => import('../views/Home/index.vue'),
          meta: { title: '首页' },
        },
        {
          path: '/setting',
          name: 'setting',
          component: () => import('../views/Setting/index.vue'),
          meta: { title: '个人中心' },
        },
        {
          path: '/courselib',
          name: 'courselib',
          component: () => import('../views/CourseLib/index.vue'),
          meta: { title: '班课' },
        },
        {
          path: '/course/:id/',
          name: 'course',
          component: () => import('../layout/Course/index.vue'),
          meta: { title: '班课' },
          children: [
            {
              path: 'index',
              name: 'course_index',
              component: () => import('../views/Course/Home/index.vue'),
              meta: { title: '班课首页' },
            },
            {
              path: 'exam',
              name: 'course_exam',
              component: () => import('../views/Course/Exam/index.vue'),
              meta: { title: '班课测验' },
            },
            {
              path: 'resource',
              name: 'course_resource',
              component: () => import('../views/Course/Resource/index.vue'),
              meta: { title: '班课资源' },
            },
            {
              path: 'student',
              name: 'course_student',
              component: () => import('../views/Course/Student/index.vue'),
              meta: { title: '班课成员' },
            },
            {
              path: 'setting',
              name: 'course_setting',
              component: () => import('../views/Course/Setting/index.vue'),
              meta: { title: '班课详情' },
            },
            {
              path: 'analysis',
              name: 'analysis',
              component: () => import('../views/Course/Analysis/index.vue'),
              meta: { title: '学情分析' },
            },
            {
              path: 'assistant',
              name: 'assistant',
              component: () => import('../views/Course/Assistant/index.vue'),
              meta: { title: '智慧助教' },
            },
            {
              path: 'tag/:tagId/practice',
              name: 'practice',
              component: () => import('../views/Course/Practice/index.vue'),
              meta: { title: '知识点练习' },
            },
          ]
        },
        {
          path: '/exam/:id/',
          name: 'exam',
          component: () => import('../layout/Exam/index.vue'),
          meta: { title: '测验' },
          children: [
            {
              path: 'preview',
              name: 'exam_preview',
              component: () => import('../views/Exam/Preview/index.vue'),
              meta: { title: '测验预览' },
            },
            {
              path: 'ranking',
              name: 'exam_ranking',
              component: () => import('../views/Exam/Ranking/index.vue'),
              meta: { title: '测验排名' },
            },
            {
              path: 'statistics',
              name: 'exam_statistics',
              component: () => import('../views/Exam/Statistics/index.vue'),
              meta: { title: '测验统计' },
            },
          ]
        },
      ],
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login/index.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register/index.vue'),
      meta: { title: '注册' },
    },
    {
      path: '/exam/:id/testing',
      name: 'exam_testing',
      component: () => import('../views/Exam/Testing/index.vue'),
      meta: { title: '在线测验' },
    },
    {
      path: '/exam/:id/result',
      name: 'exam_result',
      component: () => import('../views/Exam/Result/index.vue'),
      meta: { title: '测验结果' },
    },
  ]
})
export default router
