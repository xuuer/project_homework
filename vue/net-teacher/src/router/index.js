import { createWebHistory, createRouter } from 'vue-router'
import Index from '../layout/Common/index.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/index',
      component: Index,
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
              path: 'exam',
              name: 'course_exam',
              component: () => import('../views/Course/Exam/index.vue'),
              meta: { title: '班课测验' },
            },
            {
              path: 'homework',
              name: 'course_homework',
              component: () => import('../views/Course/Homework/index.vue'),
              meta: { title: '班课作业' },
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
              path: 'student/:studentId',
              name: "course_student_analysis",
              component: () => import('../views/Course/Student/stuAnal.vue'),
              meta: { title: '学生详情' },
            },
            {
              path: 'setting',
              name: 'course_setting',
              component: () => import('../views/Course/Setting/index.vue'),
              meta: { title: '班课设置' },
            },
            {
              path: 'tag',
              name: 'course_tag',
              component: () => import('../views/Course/Tag/index.vue'),
              meta: { title: '班课知识图谱' },
            },
          ]
        },
        {
          path: '/resourcelib',
          name: 'resourcelib',
          component: () => import('../views/ResourceLib/index.vue'),
          meta: { title: '资源库' },
        },
        {
          path: '/questionlib',
          name: 'questionlib',
          component: () => import('../views/QuestionLib/index.vue'),
          meta: { title: '题库' },
        },
        {
          path: '/question/add',
          name: 'question_add',
          component: () => import('../views/QuestionLib/addQue.vue'),
          meta: { title: '添加题目' },
        },
        {
          path: '/question/edit/:id',
          name: 'question_edit',
          component: () => import('../views/QuestionLib/editQue.vue'),
          meta: { title: '编辑题目' },
        },
        {
          path: '/question/detail/:id',
          name: 'question_detail',
          component: () => import('../views/QuestionLib/viewQue.vue'),
          meta: { title: '题目详情' },
        },
        {
          path: '/taglib',
          name: 'taglib',
          component: () => import('../views/TagLib/index.vue'),
          meta: { title: '知识库' },
        },
        {
          path: '/:type/category',
          name: 'category',
          component: () => import('../views/Category/index.vue'),
          meta: { title: '分组管理' },
        },
        {
          path: '/examlib',
          name: 'examlib',
          component: () => import('../views/ExamLib/index.vue'),
          meta: { title: '测验库' },
        },
        // 新增作业管理路由
        {
          path: '/homeworklib',
          name: 'homeworklib',
          component: () => import('../views/HomeworkLib/index.vue'),
          meta: { title: '作业库' },
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
              path: 'paper',
              name: 'exam_paper',
              component: () => import('../views/Exam/Paper/index.vue'),
              meta: { title: '测验试卷' },
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
            {
              path: 'review',
              name: 'exam_review',
              component: () => import('../views/Exam/Review/index.vue'),
              meta: { title: '测验批阅' },
            },
            {
              path: 'setting',
              name: 'exam_setting',
              component: () => import('../views/Exam/Setting/index.vue'),
              meta: { title: '测验设置' },
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
      path: '/exam/:id/student/:studentId',
      name: 'exam_result',
      component: () => import('../views/Exam/Review/reviewPaper.vue'),
      meta: { title: '测验' },
    },
    {
      path: '/exam/:id/paper/edit',
      name: 'paper_edit',
      component: () => import('../views/Exam/Paper/editPaper.vue'),
      meta: { title: '测验' },
    },
    {
      path: '/homework/:id/',
      name: 'homework',
      component: () => import('../layout/Homework/index.vue'),
      meta: { title: '作业' },
      children: [
        {
          path: 'detail',
          name: 'homework_detail',
          component: () => import('../views/Homework/Detail/index.vue'),
          meta: { title: '作业详情' },
        },
        {
          path: 'students',
          name: 'homework_students',
          component: () => import('../views/Homework/Students/index.vue'),
          meta: { title: '学生作业' },
        },
        {
          path: 'setting',
          name: 'homework_setting',
          component: () => import('../views/Homework/Setting/index.vue'),
          meta: { title: '作业设置' },
        },
      ]
    },
    {
      path: '/homework/:homeworkId/student/:studentId',
      name: 'homework_student_detail',
      component: () => import('../views/Homework/Students/detail.vue'),
      meta: { title: '学生作业详情' },
    }
  ]
})
export default router