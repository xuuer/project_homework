import request from '@/utils/request'

// ===== 用户认证相关 =====

// 学生登录
export function studentLogin(data) {
  return request.post('/student/login', data)
}

// 学生注册
export function studentRegister(data) {
  return request.post('/student/register', data)
}

// 教师登录
export function teacherLogin(data) {
  return request.post('/teacher/login', data)
}

// 教师注册
export function teacherRegister(data) {
  return request.post('/teacher/register', data)
}

// 获取学生信息
export function getStudent() {
  return request.get('/student')
}

// 获取教师信息
export function getTeacher() {
  return request.get('/teacher')
}

// 修改学生信息
export function changeStudentInfo(data) {
  return request.put('/student', data)
}

// 修改教师信息
export function changeTeacherInfo(data) {
  return request.put('/teacher', data)
}

// 修改学生密码
export function changeStudentPassword(data) {
  return request.put('/student/password', data)
}

// 修改教师密码
export function changeTeacherPassword(data) {
  return request.put('/teacher/password', data)
}

// 删除学生账户
export function deleteStudent() {
  return request.delete('/student')
}

// 删除教师账户
export function deleteTeacher() {
  return request.delete('/teacher')
}

// 兼容性函数（保持向后兼容，但建议使用具体的函数）
export function login(data, userType = 'student') {
  if (userType === 'teacher') {
    return teacherLogin(data)
  }
  return studentLogin(data)
}

export function register(data, userType = 'student') {
  if (userType === 'teacher') {
    return teacherRegister(data)
  }
  return studentRegister(data)
}

// 获取用户信息（兼容性函数）
export function getAdmin(userType = 'student') {
  if (userType === 'teacher') {
    return getTeacher()
  }
  return getStudent()
}

// 修改用户信息（兼容性函数）
export function changeAdminInfo(data, userType = 'student') {
  if (userType === 'teacher') {
    return changeTeacherInfo(data)
  }
  return changeStudentInfo(data)
}

// 修改用户密码（兼容性函数）
export function changeAdminPassword(data, userType = 'student') {
  if (userType === 'teacher') {
    return changeTeacherPassword(data)
  }
  return changeStudentPassword(data)
}

// ===== 文件上传相关 =====
export function uploadPictureURL() {
  return '/api/resource/upload'
}

export function download(params) {
  return request.get('/resource/' + params.id + '/download', {
    responseType: 'blob'  // 重点！告诉 axios 期望二进制流响应
  })
}
// export function download(params) {
//   return request.get('/resource/' + params.id + '/download')
// }

// ===== 班课相关 =====
export function getCourseList(params) {
  return request.get('/course', { params })
}

export function getCourse(params) {
  return request.get('/course/' + params.id)
}

export function addCourse(data) {
  return request.post('/course', data)
}

export function editCourse(data) {
  return request.put('/course', data)
}

export function delCourse(data) {
  return request.delete('/course/' + data.id)
}

// ===== 班课学生相关 =====
export function getCourseStudentList(params) {
  return request.get('/course/' + params.id + '/student', { params })
}

export function addCourseStudent(data) {
  return request.post('/course/' + data.id + '/student', data.studentIds)
}

export function delCourseStudent(data) {
  return request.delete('/course/' + data.id + '/student/' + data.studentId)
}

export function exportCourseStudent(params) {
  return request.get('/course/' + params.id + '/student/download', { responseType: 'blob' })
}

// ===== 班课资源相关 =====
export function getCourseResourceList(params) {
  return request.get('/course/' + params.id + '/resource')
}

export function getCourseExamList(params) {
  return request.get('/course/' + params.id + '/exam', { params })
}

export function getCourseTag(params) {
  return request.get('/course/' + params.id + '/tag')
}

export function getCourseStudentTag(params) {
  return request.get('/course/' + params.id + '/student/' + params.studentId + '/tag')
}

export function getCourseTagResource(params) {
  return request.get('/course/' + params.courseId + '/tag/' + params.tagId + '/resource')
}

export function getCourseTagPractice(params) {
  return request.get('/course/' + params.courseId + '/tag/' + params.tagId + '/practice')
}

export function submitCourseTagPractice(data) {
  return request.post('/course/practice', data)
}

// ===== 测验相关 =====
export function getExamList(params) {
  return request.get('/exam', { params })
}

export function getExam(params) {
  return request.get('/exam/' + params.id)
}

export function addExam(data) {
  return request.post('/exam', data)
}

export function editExam(data) {
  return request.put('/exam', data)
}

export function delExam(data) {
  return request.delete('/exam/' + data.id)
}

export function startExam(params) {
  return request.get('/exam/' + params.examId + '/start')
}

export function endExam(data) {
  return request.post('/exam/' + data.examId + '/end', data.questions)
}

// ===== 测验试卷相关 =====
export function getExamQuestionList(params) {
  return request.get('/exam/' + params.id + '/question')
}

export function addExamQuestion(data) {
  return request.post('/exam/' + data.id + '/question', data.questionIds)
}

export function delExamQuestion(data) {
  return request.delete('/exam/' + data.id + '/question/' + data.questionId)
}

export function saveExamQuestion(data) {
  return request.put('/exam/' + data.id + '/question', data.questions)
}

export function exportExamQuestion(params) {
  return request.get('/exam/' + params.id + '/question/download', { responseType: 'blob' })
}

// ===== 测验成绩相关 =====
export function getExamRanking(params) {
  return request.get('/exam/' + params.id + '/ranking', { params })
}

export function exportExamRanking(params) {
  return request.get('/exam/' + params.id + '/ranking/download', { responseType: 'blob' })
}

export function getExamStatistics(params) {
  return request.get('/exam/' + params.id + '/statistics')
}

export function getStudentResultList(params) {
  return request.get('/exam/' + params.id + '/result', { params })
}

export function getExamResult(params) {
  return request.get('/exam/' + params.examId + '/student/' + params.studentId + '/result')
}

export function getMyExamResult(params) {
  return request.get('/exam/' + params.examId + '/student/result')
}

export function editExamResult(data) {
  return request.put('/exam/' + data.examId + '/student/' + data.studentId + '/result', data.questions)
}

export function exportStudentPaper(data) {
  return request.get('/exam/' + data.examId + '/student/' + data.studentId + '/download', { responseType: 'blob' })
}

export function markPaper(data) {
  return request.post('/chat/exam/' + data.examId + '/student/' + data.studentId)
}

// ===== 作业相关API =====
export function getHomework(params) {
  return request.get('/homework/' + params.id)
}

export function getHomeworkList(params) {
  return request.get('/homework', { params })
}

export function addHomework(data) {
  return request.post('/homework', data)
}

export function editHomework(data) {
  return request.put('/homework', data)
}

export function delHomework(data) {
  return request.delete('/homework/' + data.id)
}

export function addHomeworkFile(data) {
  return request.post('/homework/' + data.id + '/home', data.homeworkDto)
}

export function editHomeworkFile(data) {
  return request.put('/homework/' + data.id + '/home', data.homeworkDto)
}

export function getHomeworkDetail(params) {
  return request.get('/homework/' + params.id + '/home')
}

export function delHomeworkFile(data) {
  return request.delete('/homework/' + data.id + '/home/' + data.fileId)
}

export function getStudentHomeworkList(params) {
  return request.get('/homework/' + params.id + '/result', { params })
}

export function getStudentHomeworkDetail(params) {
  return request.get('/homework/' + params.homeworkId + '/home/' + params.studentId + '/result')
}

export function markStudentHomework(data) {
  return request.post('/homework/' + data.homeworkId + '/home/' + data.studentId + '/update', {
    score: data.score,
    comment: data.comment
  })
}

export function submitStudentHomework(data) {
  return request.post('/homework/' + data.id + '/sub', data.submissionDto)
}

export function delStudentHomeworkFile(data) {
  return request.delete('/homework/' + data.homeworkId + '/sub/' + data.fileId)
}

export function editStudentHomework(data) {
  return request.put('/homework/' + data.id + '/sub', data.submissionDto)
}

export function getMyHomeworkDetail(params) {
  return request.get('/homework/' + params.id + '/home/result')
}

export function markHomeworkAI(data) {
  return request.post('/chat/homework/' + data.homeworkId + '/student/' + data.studentId)
}

export function getCourseHomeworkList(params) {
  return request.get('/course/' + params.id + '/homework', { params })
}

export function getTeacherHomeworkCount() {
  return request.get('/course/teacher/homework/statistics')
}

export function getCourseHomeworkCount(params) {
  return request.get('/course/' + params.id + '/homework/count')
}

// ===== 资源相关 =====
export function getResourceList(params) {
  return request.get('/resource', { params })
}

export function getResource(params) {
  return request.get('/resource/' + params.id)
}

export function addResource(data) {
  return request.post('/resource', data)
}

export function editResource(data) {
  return request.put('/resource', data)
}

export function delResource(data) {
  return request.delete('/resource/' + data.id)
}

export function downResource(params) {
  return request.get('/resource/' + params.id + '/download', { responseType: 'blob' })
}

export function previewResource(params) {
  return request.get('/resource/' + params.id + '/preview')
}

// ===== 题目相关 =====
export function getQuestionList(params) {
  return request.get('/question', { params })
}

export function getQuestion(params) {
  return request.get('/question/' + params.id)
}

export function addQuestion(data) {
  return request.post('/question', data)
}

export function editQuestion(data) {
  return request.put('/question', data)
}

export function delQuestion(data) {
  return request.delete('/question/' + data.ids, { data: data.ids })
}

// ===== 分类相关 =====
export function getCategoryTree(params) {
  return request.get('/category', { params })
}

export function getCategory(params) {
  return request.get('/category/' + params.id)
}

export function addCategory(data) {
  return request.post('/category', data)
}

export function editCategory(data) {
  return request.put('/category', data)
}

export function delCategory(data) {
  return request.delete('/category/' + data.id)
}

export function addCategoryResource(data) {
  return request.post('/category/' + data.id + '/resource', data.resourceIds)
}

export function delCategoryResource(data) {
  return request.delete('/category/' + data.id + '/resource/' + data.resourceId)
}

// ===== AI聊天相关 =====
export function getChatHistory(params) {
  return request.get('/chat/history/' + params.courseId)
}

export function deleteChatHistory(params) {
  return request.delete('/chat/history/' + params.courseId)
}

export function sendChatMessage(data) {
  return request.post('/chat/brain', data)
}

export function sendChatMessageStream(data) {
  return request.post('/chat/coze/stream', data)
}

// ===== 知识图谱相关（如果有的话） =====
export function getTag(params) {
  return request.get('/tag', { params })
}

export function getNode(params) {
  return request.get('/tag/node/' + params.id)
}

export function addNode(data) {
  return request.post('/tag/node', data)
}

export function editNode(data) {
  return request.put('/tag/node', data)
}

export function delNode(data) {
  return request.delete('/tag/node/' + data.id)
}

export function getRelationship(params) {
  return request.get('/tag/relationship/' + params.id)
}

export function addRelationship(data) {
  return request.post('/tag/relationship', data)
}

export function editRelationship(data) {
  return request.put('/tag/relationship', data)
}

export function delRelationship(data) {
  return request.delete('/tag/relationship/' + data.id)
}