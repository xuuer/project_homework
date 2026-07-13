package edu.fdzc.project.service;

import edu.fdzc.project.entity.Exam;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.dto.StatisticsDto;
import edu.fdzc.project.entity.vo.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【exam】的数据库操作Service
* @createDate 2025-06-21 17:07:33
*/
public interface ExamService extends IService<Exam> {

    ExamVo selectExamById(Long id);

    PageVo<Exam> selectPage(Long pageNum, Long pageSize, Exam exam);

    Object insert(Exam exam);

    Object deleteById(Long id);

    StartExamVo startExam(Long examId);

    Object endExam(Long examId, List<QuestionDto> studentQuestions);

    PageVo<StudentExamVo> selectStudentResultList(Long id, Long pageNum, Long pageSize, String status);

    StudentExamResultVo selectStudentResult(Long studentId, Long examId);

    Object editStudentResult(Long studentId, Long examId, List<QuestionDto> questions);

    void downloadStudentPaper(HttpServletResponse response, Long examId, Long studentId);

    List<QuestionDto> selectQuestion(Long id);

    Object insertQuestion(Long id, Long[] questionIds);

    Object updateQuestion(Long id, List<QuestionDto> questions);

    Object deleteQuestion(Long id, Long questionId);

    void downloadQuestion(HttpServletResponse response, Long id);

    PageVo<StudentExamVo> selectRanking(Long id, Long pageNum, Long pageSize);

    void downloadRanking(HttpServletResponse response, Long id);

    StatisticsDto selectStatistics(Long id);

    Object updateByExamId(Exam exam);
}
