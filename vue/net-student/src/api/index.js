import request from '../utils/request'
import config from '../config'

// 用户信息相关

// 登录
export function login(data) {
  return request.post('/student/login', data)
}

// 注册
export function register(data) {
  return request.post('/student/register', data)
}

// 获取用户信息
export function getAdmin() {
  return request.get('/student')
}

// 修改基本资料
export function changeAdminInfo(data) {
  return request.put('/student', data)
}

// 修改密码
export function changeAdminPassword(data) {
  return request.put('/student/password', data)
}

// 更新图片地址
export function uploadPictureURL() {
  return config.baseURL + '/resource/upload'
}

// 班课相关

// 获取班课列表
export function getCourseList(params) {
  return request.get('/course', { params })
}

// 获取班课信息
export function getCourse(params) {
  return request.get('/course/' + params.id)
}

// 班课学生相关

// 获取班课学生列表
export function getCourseStudentList(params) {
  return request.get('/course/' + params.id + '/student', { params })
}

// 班课资源相关

// 获取班课资源列表
export function getCourseResourceList(params) {
  return request.get('/course/' + params.id + '/resource')
}

// 班课测验相关

// 获取班课测验列表
export function getCourseExamList(params) {
  return request.get('/course/' + params.id + '/exam', { params })
}

// 获取测验
export function getExam(params) {
  return request.get('/exam/' + params.id)
}

// 开始测验
export function startExam(params) {
  return request.get('/exam/' + params.examId + '/start')
}

// 结束测验
export function endExam(data) {
  return request.post('/exam/' + data.examId + '/end', data.questions)
}

// 获取测验结果
export function getExamResult(params) {
  return request.get('/exam/' + params.examId + '/student/result')
}

// 资源库相关

// 下载资源
export function downResource(params) {
  return request.get('/resource/' + params.id + '/download', { responseType: 'blob' })
}

// 预览资源
export function previewResource(params) {
  return request.get('/resource/' + params.id + '/preview')
}

// 试题库相关

// 获取试题
export function getQuestion(params) {
  return request.get('/question/' + params.id)
}

// 试卷相关

// 获取试卷题目
export function getExamQuestionList(params) {
  return request.get('/exam/' + params.id + '/question')
}

// 测验学生相关

// 获取测验学生列表
export function getExamRanking(params) {
  return request.get('/exam/' + params.id + '/ranking', { params })
}

// 获取测验统计
export function getExamStatistics(params) {
  return request.get('/exam/' + params.id + '/statistics')
}

// 知识点相关

// 获取知识图谱
export function getTagGraph(params) {
  return request.get('/course/' + params.id + '/student/tag')
}

// 获取知识点
export function getTag(params) {
  return request.get('/tag/' + params.id)
}

// 获取知识点资源
export function getTagResource(params) {
  return request.get('/course/' + params.id + '/tag/' + params.tagId + '/resource')
}

// 获取知识点练习
export function getTagPractice(params) {
  return request.get('/course/' + params.id + '/tag/' + params.tagId + '/practice')
}

// 提交知识点练习
export function submitTagPractice(data) {
  return request.post('/course/practice', data)
}

// 智慧助教相关

// 获取脑图
export function getBrain(data) {
  return request.post('/chat/brain', data)
}

// 获取历史对话
export function getChatHistory(params) {
  return request.get('/chat/history/' + params.id)
}

// 清空历史对话
export function delChatHistory(params) {
  return request.delete('/chat/history/' + params.id)
}

// 作业相关接口

// 获取课程作业列表
export function getCourseHomeworkList(params) {
  return request.get('/course/' + params.id + '/homework', { params })
}

// 根据作业ID获取作业详情
export function getHomework(params) {
  return request.get('/homework/' + params.id)
}

// 获取作业的详细信息（包括附件）
export function getHomeworkDetail(params) {
  return request.get('/homework/' + params.id + '/home')
}

// 学生提交作业
export function submitHomework(id, data) {
  return request.post('/homework/' + id + '/sub', data)
}

// 学生修改作业
export function updateHomework(id, data) {
  return request.put('/homework/' + id + '/sub', data)
}

// 学生获取自己的作业详情/结果
export function getMyHomeworkResult(params) {
  return request.get('/homework/' + params.id + '/home/result')
}

// 删除作业文件
export function deleteHomeworkFile(homeworkId, fileId) {
  return request.delete('/homework/' + homeworkId + '/sub/' + fileId)
}
