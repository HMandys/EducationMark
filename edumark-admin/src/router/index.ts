import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import { useUserStore } from '@/store/user'

// 布局组件
const Layout = () => import('@/layouts/DefaultLayout.vue')

// 公开路由（无需登录）
export const publicRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在', hidden: true },
  },
]

// 需要认证的路由
export const authRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer', affix: true },
      },
    ],
  },
  {
    path: '/school',
    component: Layout,
    redirect: '/school/list',
    meta: { title: '组织管理', icon: 'School' },
    children: [
      {
        path: 'list',
        name: 'SchoolList',
        component: () => import('@/views/school/school/index.vue'),
        meta: { title: '学校管理', icon: 'OfficeBuilding', permission: 'school:school:list' },
      },
      {
        path: 'grade',
        name: 'GradeList',
        component: () => import('@/views/school/grade/index.vue'),
        meta: { title: '年级管理', icon: 'Collection', permission: 'school:grade:list' },
      },
      {
        path: 'class',
        name: 'ClassList',
        component: () => import('@/views/school/class/index.vue'),
        meta: { title: '班级管理', icon: 'Files', permission: 'school:class:list' },
      },
      {
        path: 'teacher',
        name: 'TeacherList',
        component: () => import('@/views/school/teacher/index.vue'),
        meta: { title: '教师管理', icon: 'Avatar', permission: 'school:teacher:list' },
      },
      {
        path: 'student',
        name: 'StudentList',
        component: () => import('@/views/school/student/index.vue'),
        meta: { title: '学生管理', icon: 'User', permission: 'school:student:list' },
      },
      {
        path: 'parent',
        name: 'ParentList',
        component: () => import('@/views/school/parent/index.vue'),
        meta: { title: '家长管理', icon: 'UserFilled', permission: 'school:parent:list' },
      },
    ],
  },
  {
    path: '/exam',
    component: Layout,
    redirect: '/exam/list',
    meta: { title: '考试管理', icon: 'Document' },
    children: [
      {
        path: 'list',
        name: 'ExamList',
        component: () => import('@/views/exam/index.vue'),
        meta: { title: '考试管理', icon: 'Tickets', permission: 'exam:exam:list' },
      },
      {
        path: 'workbench/:id',
        name: 'ExamWorkbench',
        component: () => import('@/views/exam/workbench.vue'),
        meta: { title: '考试工作台', icon: 'Monitor', hidden: true, permission: 'exam:exam:list' },
      },
      {
        path: 'knowledge-point',
        name: 'KnowledgePointList',
        component: () => import('@/views/exam/knowledge-point/index.vue'),
        meta: { title: '知识点管理', icon: 'Collection', permission: 'exam:subject:list' },
      },
      {
        path: 'answer-sheet',
        name: 'AnswerSheetList',
        component: () => import('@/views/answer-sheet/index.vue'),
        meta: { title: '答题卡管理', icon: 'Picture', permission: 'exam:answer:list' },
      },
      {
        path: 'answer-sheet/objective-review/:id',
        name: 'AnswerSheetObjectiveReview',
        component: () => import('@/views/answer-sheet/objective-review.vue'),
        meta: { title: '客观题复核', icon: 'Checked', hidden: true, permission: 'exam:answer:list' },
      },
      {
        path: 'answer-sheet/subjective-review/:id',
        name: 'AnswerSheetSubjectiveReview',
        component: () => import('@/views/answer-sheet/subjective-review.vue'),
        meta: { title: '主观题裁题核验', icon: 'Crop', hidden: true, permission: 'exam:answer:list' },
      },
    ],
  },
  {
    path: '/answer-sheet-design',
    component: Layout,
    redirect: '/answer-sheet-design/list',
    meta: { title: '答题卡设计', icon: 'DocumentCopy' },
    children: [
      {
        path: 'list',
        name: 'AnswerSheetTemplateList',
        component: () => import('@/views/answer-sheet-design/index.vue'),
        meta: { title: '模板列表', icon: 'List', permission: 'exam:answer:list' },
      },
      {
        path: 'edit/:id?',
        name: 'AnswerSheetTemplateEdit',
        component: () => import('@/views/answer-sheet-design/editor.vue'),
        meta: { title: '模板编辑', icon: 'Edit', hidden: true, permission: 'exam:answer:list' },
      },
    ],
  },
  {
    path: '/marking',
    component: Layout,
    redirect: '/marking/task',
    meta: { title: '阅卷管理', icon: 'EditPen' },
    children: [
      {
        path: 'task',
        name: 'MarkingTask',
        component: () => import('@/views/marking/task.vue'),
        meta: { title: '阅卷任务', icon: 'List', permission: 'marking:task:list' },
      },
      {
        path: 'workspace',
        name: 'MarkingWorkspace',
        component: () => import('@/views/marking/workspace.vue'),
        meta: { title: '阅卷工作台', icon: 'Edit', permission: 'marking:work:view' },
      },
    ],
  },
  {
    path: '/score',
    component: Layout,
    redirect: '/score/list',
    meta: { title: '成绩管理', icon: 'DataAnalysis' },
    children: [
      {
        path: 'list',
        name: 'ScoreList',
        component: () => import('@/views/score/index.vue'),
        meta: { title: '成绩查询', icon: 'List', permission: 'score:summary:view' },
      },
      {
        path: 'publish-check/:id',
        name: 'ScorePublishCheck',
        component: () => import('@/views/score/publish-check.vue'),
        meta: { title: '出分检查', icon: 'CircleCheck', hidden: true, permission: 'score:summary:view' },
      },
    ],
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', permission: 'system:user:list' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', permission: 'system:role:list' },
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu', permission: 'system:menu:list' },
      },
    ],
  },
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: [...publicRoutes, ...authRoutes],
  scrollBehavior: () => ({ left: 0, top: 0 }),
})

// 白名单路径
const whiteList = ['/login', '/404']

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || ''} - EduMark`

  const userStore = useUserStore()

  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 未登录跳转登录页
  if (!userStore.isLoggedIn) {
    next(`/login?redirect=${to.path}`)
    return
  }

  // 权限检查
  const permission = to.meta.permission as string | undefined
  if (permission && !userStore.hasPermission(permission)) {
    next('/404')
    return
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

// 捕获所有未匹配的路由
router.addRoute({
  path: '/:pathMatch(.*)*',
  redirect: '/404',
})

export default router
