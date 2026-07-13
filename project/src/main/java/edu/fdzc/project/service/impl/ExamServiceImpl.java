package edu.fdzc.project.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.common.Code;
import edu.fdzc.project.entity.*;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.dto.StatisticsDto;
import edu.fdzc.project.entity.excel.ExamQuestionExcel;
import edu.fdzc.project.entity.excel.ExamRankingExcel;
import edu.fdzc.project.entity.excel.StudentPaperExcel;
import edu.fdzc.project.entity.vo.*;
import edu.fdzc.project.exception.CustomException;
import edu.fdzc.project.mapper.*;
import edu.fdzc.project.service.ExamService;
import edu.fdzc.project.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author zhangjiu
* @description 针对表【exam】的数据库操作Service实现
* @createDate 2025-06-21 17:07:33
*/
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
    implements ExamService{

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    public StudentExamMapper studentExamMapper;

    @Autowired
    public StudentMapper studentMapper;

    @Autowired
    public StudentTagMapper studentTagMapper;

    @Autowired
    public StudentExamQuestionMapper studentExamQuestionMapper;

    @SneakyThrows
    @Override
    public ExamVo selectExamById(Long id) {
        Exam  exam = examMapper.selectById(id);
        if(exam == null){
            throw new CustomException(Code.LOGICAL_ERROR,"测验不存在");
        }
        List<Map<String, Object>> maps = examQuestionMapper.selectMaps(
                new QueryWrapper<ExamQuestion>()
                        .select("count(*) question_num", "sum(score) total_score")
                        .eq("exam_id", id));
        Long questionNum = (Long) maps.get(0).get("question_num");
        Double totalScore = (Double) maps.get(0).get("total_score");
        return getExamVo(exam, questionNum, totalScore); //调用下面的方法，封装进入这个ExamVo中

    }
    @NotNull
    private static ExamVo getExamVo(Exam exam, Long questionNum, Double totalScore) {
        ExamVo vo = new ExamVo();
        vo.setId(exam.getId());
        vo.setName(exam.getName());
        vo.setDescription(exam.getDescription());
        vo.setOpenTime(exam.getOpenTime());
        vo.setCloseTime(exam.getCloseTime());
        vo.setDuration(exam.getDuration());
        vo.setQuestionNum(questionNum);//设置题目总数
        vo.setTotalScore(totalScore); // 总分数
        vo.setScreenCutTime(exam.getScreenCutTime());
        vo.setScreenCutNum(exam.getScreenCutNum());
        vo.setCreateTime(exam.getCreateTime());
        vo.setUpdateTime(exam.getUpdateTime());
        return vo;
    }


    @Override
    public PageVo<Exam> selectPage(Long pageNum, Long pageSize, Exam exam) {
        //教师id
        Long id = JwtUtil.getUserId();
        //测验名
        String name = exam.getName();

        //查询条件
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        //教师ID
        wrapper.eq(Exam::getTeacherId, id);
        //模糊查询name
        if(name != null && !name.isEmpty()){
            wrapper.like(Exam::getName, name);
        }
        //排序
        wrapper.orderByAsc(Exam::getCloseTime);

        //执行分页查询
        Page<Exam> res  = examMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        PageVo<Exam> vo = new PageVo<>();

        vo.setList(res.getRecords());
        vo.setTotal(res.getTotal());
        return vo;
    }

    @Override
    public Object insert(Exam exam) {
        Long id = JwtUtil.getUserId();
        //设置老师的id
        exam.setTeacherId(id);
        // 设置时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);

        exam.setCreateTime(createTime);
        return examMapper.insert(exam);
    }

    @Override
    public Object deleteById(Long id) {
        return examMapper.deleteById(id);
    }

    @SneakyThrows
    @Override
    public StartExamVo startExam(Long examId) {
        Long studentId = JwtUtil.getUserId();
        System.out.println(studentId);
        // 测验信息调用前面的方法 selectExamById
        ExamVo exam = selectExamById(examId);

        // 检查开始时间是否合法
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(exam.getOpenTime());
        if(now.isBefore(LocalDateTime.parse(exam.getOpenTime(), formatter))) {
            throw new CustomException(Code.EXAM_ERROR, "测验未开始");
        }
        if(now.isAfter(LocalDateTime.parse(exam.getCloseTime(), formatter))) {
            throw new CustomException(Code.EXAM_ERROR, "测验已关闭");
        }

        //------------------------------------------------
        // 检查是否已经测验过
        StudentExam res = studentExamMapper.selectOne(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, examId)
                        .eq(StudentExam::getStudentId, studentId));
        if(res != null && res.getEndTime() != null) {
            throw new CustomException(Code.EXAM_ERROR, "已经测验过了");
        }

        // 计算开始时间
        String startTime, endTime;
        if(res == null) {
            startTime = now.format(formatter);
        } else {
            startTime = res.getStartTime();
        }

        // 计算收卷时间
        if(exam.getDuration() == null) {
            endTime = exam.getCloseTime();
        } else {
            LocalDateTime duraEnd = now.plusMinutes(exam.getDuration());
            LocalDateTime examEnd = LocalDateTime.parse(exam.getCloseTime(), formatter);
            LocalDateTime finalEnd = duraEnd.isBefore(examEnd) ? duraEnd : examEnd;
            endTime = finalEnd.format(formatter);
        }

        if(res == null) {
            StudentExam se = new StudentExam();
            se.setExamId(examId);
            se.setStudentId(studentId);
            se.setStartTime(startTime);
            se.setStatus("进行中");
            studentExamMapper.insert(se);
        }

        // 学生信息
        Student student = studentMapper.selectById(studentId);

        // 试卷信息
        List<QuestionDto> paper = examQuestionMapper.selectQuestion(examId);
        for(QuestionDto question : paper) {
            question.setAnswer(null);
            question.setAnalysis(null);
        }

        StartExamVo vo = new StartExamVo();
        vo.setExam(exam);
        vo.setStudent(student);
        vo.setEndTime(endTime);
        vo.setPaper(paper);
        return vo;
// ------------------------------------------------
    }

    @SneakyThrows
    @Override
    public Object endExam(Long examId, List<QuestionDto> studentQuestions) {
        Long studentId = JwtUtil.getUserId();
        System.out.println(studentId);

        // 计算交卷时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String endTime = now.format(formatter);

        // 检查交卷时间是否合法
        StudentExam res = studentExamMapper.selectOne(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, examId)
                        .eq(StudentExam::getStudentId, studentId));
        if(res == null) {
            throw new CustomException(Code.EXAM_ERROR, "未开始考试");
        }

        // 计算成绩
        double totalScore = 0.0;
        boolean isAllMarked = true;
        List<QuestionDto> teacherQuestions = examQuestionMapper.selectQuestion(examId);

        for(int i = 0; i < teacherQuestions.size(); i ++){
            QuestionDto q = teacherQuestions.get(i);
            QuestionDto sq = studentQuestions.get(i);
            Double score = null;

            if(!sq.getType().equals("综合题")) {
                // 计算本题得分
                Long rightNum = 0L;
                score = 0.0;
                if (q.getAnswer().equals(sq.getStudentAnswer())) {
                    rightNum = 1L;
                    score = q.getScore();
                }

                // 记录知识点正确率
                List<String> tags = q.getTags();
                if (tags != null) {
                    for (String tag : tags) {
                        StudentTag st = studentTagMapper.selectOne(
                                new LambdaQueryWrapper<StudentTag>()
                                        .eq(StudentTag::getStudentId, studentId)
                                        .eq(StudentTag::getTag, tag));
                        if (st == null) {
                            st = new StudentTag();
                            st.setStudentId(studentId);
                            st.setTag(tag);
                            st.setRightNum(rightNum);
                            st.setTotalNum(1L);
                            studentTagMapper.insert(st);
                        } else {
                            st.setRightNum(st.getRightNum() + rightNum);
                            st.setTotalNum(st.getTotalNum() + 1);
                            studentTagMapper.updateById(st);
                        }
                    }
                }
                // 汇总各题得分
                totalScore += score;
            } else {
                isAllMarked = false;
            }

            // 记录本题得分
            StudentExamQuestion seq = new StudentExamQuestion();
            seq.setStudentId(studentId);
            seq.setExamId(examId);
            seq.setQuestionId(q.getId());
            seq.setScore(score);
            seq.setAnswer(sq.getStudentAnswer());
            studentExamQuestionMapper.insert(seq);
        }

        // 试卷留档
        res.setEndTime(endTime);
        res.setScore(totalScore);
        res.setStatus(isAllMarked ? "已批阅" : "待批阅");
        return studentExamMapper.updateById(res);
    }

    @Override
    public PageVo<StudentExamVo> selectStudentResultList(Long id, Long pageNum, Long pageSize, String status) {
        // 拼接查询条件
        LambdaQueryWrapper<StudentExam> query = new LambdaQueryWrapper<>();
        query.eq(StudentExam::getExamId, id);
        query.isNotNull(StudentExam::getEndTime);
        if(status != null && !status.isEmpty()) {
            query.eq(StudentExam::getStatus, status);
        }
        // 执行查询
        Page<StudentExam> page = studentExamMapper.selectPage(new Page<>(pageNum, pageSize), query);
        // 获取学生信息
//        System.out.println(page.getRecords());
        List<StudentExamVo> list = new ArrayList<>();
        List<StudentExam> records = page.getRecords();
        for(StudentExam record : records) {
            System.out.println(record);
            System.out.println(record.getStudentId());
            Student student = studentMapper.selectById(record.getStudentId());
//            Student student = studentMapper.selectById("1873316935328509959");
            System.out.println(student);
            StudentExamVo vo = new StudentExamVo();
            vo.setId(student.getId());
            vo.setNo(student.getNo());
            vo.setName(student.getName());
            vo.setScore(record.getScore());
            vo.setStartTime(record.getStartTime());
            vo.setEndTime(record.getEndTime());
            vo.setStatus(record.getStatus());
            list.add(vo);
        }
        // 返回查询结果
        PageVo<StudentExamVo> vo = new PageVo<>();
        vo.setList(list);
        vo.setTotal(page.getTotal());
        return vo;
    }

    @SneakyThrows
    @Override
    public StudentExamResultVo selectStudentResult(Long studentId, Long examId) {
        System.out.println(studentId+" "+examId);
        StudentExamVo studentExamVo = studentExamMapper.selectExam(studentId, examId);
        System.out.println(studentExamVo);
        if(studentExamVo == null || studentExamVo.getEndTime() == null) {
            throw new CustomException(Code.EXAM_ERROR, "还没完成测验");
        }
        // 查询测验信息
        ExamVo exam = selectExamById(examId);
        // 查询试卷信息
        List<QuestionDto> paper = studentExamQuestionMapper.selectQuestion(studentId, examId);
        // 返回查询结果
        StudentExamResultVo vo = new StudentExamResultVo();
        vo.setStudent(studentExamVo);
        vo.setExam(exam);
        vo.setPaper(paper);
        return vo;
    }

    @Override
    public Object editStudentResult(Long studentId, Long examId, List<QuestionDto> questions) {
        double totalScore = 0.0;
        boolean isAllMarked = true;
        for (QuestionDto q : questions) {

            Double score = q.getStudentScore();
            if(score == null) {
                isAllMarked = false;
                continue;
            }

            if (q.getType().equals("综合题")) {
                // 计算本题得分
                Long rightNum = 0L;
                if (q.getStudentScore().equals(q.getScore())) {
                    rightNum = 1L;
                }

                StudentExamQuestion res = studentExamQuestionMapper.selectOne(
                        new LambdaQueryWrapper<StudentExamQuestion>()
                                .eq(StudentExamQuestion::getStudentId, studentId)
                                .eq(StudentExamQuestion::getExamId, examId)
                                .eq(StudentExamQuestion::getQuestionId, q.getId()));

                res.setRemark(q.getRemark());

                if(res.getScore() == null) {
                    // 记录知识点正确率
                    List<String> tags = q.getTags();
                    if (tags != null) {
                        for (String tag : tags) {
                            StudentTag st = studentTagMapper.selectOne(
                                    new LambdaQueryWrapper<StudentTag>()
                                            .eq(StudentTag::getStudentId, studentId)
                                            .eq(StudentTag::getTag, tag));
                            if (st == null) {
                                st = new StudentTag();
                                st.setStudentId(studentId);
                                st.setTag(tag);
                                st.setRightNum(rightNum);
                                st.setTotalNum(1L);
                                studentTagMapper.insert(st);
                            } else {
                                st.setRightNum(st.getRightNum() + rightNum);
                                st.setTotalNum(st.getTotalNum() + 1);
                                studentTagMapper.updateById(st);
                            }
                        }
                    }
                }

                // 记录本题得分
                res.setScore(score);
                studentExamQuestionMapper.updateById(res);
            }
            totalScore += score;
        }

        // 试卷留档
        StudentExam se = new StudentExam();
        se.setScore(totalScore);
        se.setStatus(isAllMarked ? "已批阅" : "待批阅");
        return studentExamMapper.update(
                se,
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getStudentId, studentId)
                        .eq(StudentExam::getExamId, examId));
    }

    @Override
    public void downloadStudentPaper(HttpServletResponse response, Long examId, Long studentId) {
        Exam exam = examMapper.selectById(examId);
        Student student = studentMapper.selectById(studentId);
        String fileName = exam.getName() + "_" + student.getName() + "答卷.xlsx";
        List<StudentPaperExcel> list = studentExamQuestionMapper.selectExcelQuestion(examId, studentId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 将Content-Disposition属性暴露给浏览器
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        try {
            EasyExcel.write(response.getOutputStream(), StudentPaperExcel.class).autoCloseStream(Boolean.FALSE).sheet("答卷").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @SneakyThrows
    @Override
    public List<QuestionDto> selectQuestion(Long id) {
        return examQuestionMapper.selectQuestion(id);
    }

    @SneakyThrows
    @Override
    public Object insertQuestion(Long id, Long[] questionIds) {
        // 检查是否有学生已经答题
        Long count = studentExamMapper.selectCount(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, id));//考试的id
        if(count > 0) {
            throw new CustomException(Code.LOGICAL_ERROR, "已有学生答题，禁止修改试卷");
        }

        // 查询当前试卷题目数，新添加的题目应置于已添加的题目之后
        Long num = examQuestionMapper.selectCount(
                new LambdaQueryWrapper<ExamQuestion>()
                        .eq(ExamQuestion::getExamId, id));

        // 添加试卷题目
        for(int i = 0; i < questionIds.length; i ++) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(id);
            eq.setQuestionId(questionIds[i]);
            eq.setScore(1.0);
            eq.setSort(num + i +1);
            eq.setMarkError(1.0);
            int cnt = examQuestionMapper.insert(eq);
            if(cnt == 0) return 0;
        }
        return 1;
    }

    @SneakyThrows
    @Override
    public Object updateQuestion(Long examId, List<QuestionDto> questions) {
        // 检查是否有学生已经答题
        Long count = studentExamMapper.selectCount(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, examId));
        if(count > 0) {
            throw new CustomException(Code.LOGICAL_ERROR, "已有学生答题，禁止修改试卷");
        }

        for(int i = 0; i < questions.size(); i ++) {
            QuestionDto q = questions.get(i);
            ExamQuestion eq = new ExamQuestion();
            eq.setScore(q.getScore());
            eq.setSort((long) (i + 1));
            eq.setMarkError(q.getMarkError());
            int cnt = examQuestionMapper.update(eq,
                    new LambdaQueryWrapper<ExamQuestion>()
                            .eq(ExamQuestion::getExamId, examId)
                            .eq(ExamQuestion::getQuestionId, q.getId()));
            if(cnt == 0) return 0;
        }
        return 1;
    }

    @SneakyThrows
    @Override
    public Object deleteQuestion(Long examId, Long questionId) {
        // 检查是否有学生已经答题
        Long count = studentExamMapper.selectCount(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, examId));
        if(count > 0) {
            throw new CustomException(Code.LOGICAL_ERROR, "已有学生答题，禁止修改试卷");
        }

        return examQuestionMapper.delete(
                new LambdaQueryWrapper<ExamQuestion>()
                        .eq(ExamQuestion::getExamId, examId)
                        .eq(ExamQuestion::getQuestionId, questionId));
    }

    @Override
    public void downloadQuestion(HttpServletResponse response, Long examId) {
        Exam exam = examMapper.selectById(examId);
        String fileName = exam.getName() + "试卷.xlsx";
        List<ExamQuestionExcel> list = examQuestionMapper.selectExcelQuestion(examId);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 将Content-Disposition属性暴露给浏览器
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        try {
            EasyExcel.write(response.getOutputStream(), ExamQuestionExcel.class).autoCloseStream(Boolean.FALSE).sheet("试卷").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public PageVo<StudentExamVo> selectRanking(Long examId, Long pageNum, Long pageSize) {
        List<StudentExamVo> list = studentExamMapper.selectRanking(examId, pageSize, pageSize * (pageNum - 1));
        // 按成绩从高到低排名
        for(int i = 0; i < list.size(); i ++) {
            list.get(i).setRanking(pageSize * (pageNum - 1) + i + 1);
        }
        Long total = studentExamMapper.selectCount(
                new LambdaQueryWrapper<StudentExam>()
                        .eq(StudentExam::getExamId, examId));
        PageVo<StudentExamVo> vo = new PageVo<>();
        vo.setList(list);
        vo.setTotal(total);
        return vo;
    }

    @Override
    public void downloadRanking(HttpServletResponse response, Long examId) {
        Exam exam = examMapper.selectById(examId);
        String fileName = exam.getName() + "成绩排名.xlsx";
        List<ExamRankingExcel> list = studentExamMapper.selectExcelRanking(examId);
        for(int i = 0; i < list.size(); i ++) {
            list.get(i).setRanking((long) (i + 1));
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 将Content-Disposition属性暴露给浏览器
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        try {
            EasyExcel.write(response.getOutputStream(), ExamRankingExcel.class).autoCloseStream(Boolean.FALSE).sheet(exam.getName()).doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public StatisticsDto selectStatistics(Long examId) {
//        Long d1 = examId;
        StatisticsDto dto = studentExamMapper.selectStatistics(examId);
        if(dto == null) {
            dto = new StatisticsDto();
            dto.setMaxScore(0.0);
            dto.setMinScore(0.0);
            dto.setAvgScore(0.0);
        }
        return dto;
    }

    @Override
    public Object updateByExamId(Exam exam) {
        Long id = JwtUtil.getUserId();
        exam.setTeacherId(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);
        exam.setUpdateTime(updateTime);
        return examMapper.updateById(exam);
    }
}




