package edu.fdzc.project.controller;


import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Exam;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.dto.StatisticsDto;
import edu.fdzc.project.entity.vo.*;
import edu.fdzc.project.service.ExamService;
import edu.fdzc.project.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

//    查找考试详情<>
    @GetMapping("/{id}")
    public Result<ExamVo> selectExamById(@PathVariable Long id) {
        return Result.success(examService.selectExamById(id));
    }

    //获取全部的考试信息<>
    @GetMapping
    public Result<PageVo<Exam>> selectPage(@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                           Exam exam){
        return Result.success(examService.selectPage(pageNum, pageSize, exam));
    }
//  增加资源<>
    @PostMapping
    public Result<Object> insert(@RequestBody Exam exam){
        return Result.success("添加成功", examService.insert(exam));
    }

    //修改资源<>
    @PutMapping
    public Result<Object> updateById(@RequestBody Exam exam){
        return Result.success("修改成功", examService.updateByExamId(exam));
    }
    //  删除测验<>
    @DeleteMapping("/{id}")
    public Result<Object> deleteById(@PathVariable("id") Long id){
        return Result.success("删除成功", examService.deleteById(id));
    }

    //学生开始测验 <>
    @GetMapping("/{examId}/start")
    public Result<StartExamVo> startExam(@PathVariable("examId") Long examId){
        System.out.println("交卷成功");
        return Result.success(examService.startExam(examId));
    }

    //学生结束测验<>
    @PostMapping("/{examId}/end")
    public Result<Object> endExam(@PathVariable("examId") Long examId,
                                  @RequestBody List<QuestionDto> studentQuestions){
        return Result.success("交卷成功", examService.endExam(examId, studentQuestions));
    }


    //查看全部学生检验列表<>
    @GetMapping("/{id}/result")
    public Result<PageVo<StudentExamVo>> selectStudentResultList(@PathVariable("id") Long id,
                                                                 @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                                                 @RequestParam(name = "status", defaultValue = "") String status){
        return Result.success(examService.selectStudentResultList(id, pageNum, pageSize, status));
    }

    //学生获取检验的结果<>
    @GetMapping("/{examId}/student/result")
    public Result<StudentExamResultVo> selectStudentResult(@PathVariable("examId") Long examId){
        return selectStudentResult(JwtUtil.getUserId(), examId);
    }

    //获取学生的检验结果<>
    @GetMapping("/{examId}/student/{studentId}/result")
    public Result<StudentExamResultVo> selectStudentResult(@PathVariable("studentId") Long studentId,
                                                           @PathVariable("examId") Long examId){

        return Result.success(examService.selectStudentResult(studentId, examId));
    }

    //修改学生测验结果**
    @PutMapping("/{examId}/student/{studentId}/result")
    public Result<Object> editStudentResult(@PathVariable("examId") Long examId,
                                            @PathVariable("studentId") Long studentId,
                                            @RequestBody List<QuestionDto> questions){
        return Result.success("修改成功", examService.editStudentResult(studentId, examId, questions));
    }


    //导出学生答卷**
    @GetMapping("/{examId}/student/{studentId}/download")
    public void downloadStudentPaper(HttpServletResponse response,
                                     @PathVariable("examId") Long examId,
                                     @PathVariable("studentId") Long studentId) {
        examService.downloadStudentPaper(response, examId, studentId);
    }

    //获取测验的试卷<>
    @GetMapping("/{id}/question")
    public Result<List<QuestionDto>> selectQuestion(@PathVariable("id") Long id){
        return Result.success(examService.selectQuestion(id));
    }


    //添加试卷题目 <>
    @PostMapping("/{id}/question")
    public Result<Object> insertQuestion(@PathVariable("id") Long id,
                                         @RequestBody Long[] questionIds){
        return Result.success("添加成功", examService.insertQuestion(id, questionIds));
    }

//    修改试卷题目 <>
    @PutMapping("/{id}/question")
    public Result<Object> updateQuestion(@PathVariable("id") Long id,
                                         @RequestBody List<QuestionDto> questions){
        return Result.success("修改成功", examService.updateQuestion(id, questions));
    }

//    删除试卷题目<>
    @DeleteMapping("/{id}/question/{questionId}")
    public Result<Object> deleteQuestion(@PathVariable("id") Long id,
                                         @PathVariable("questionId") Long questionId){
        return Result.success("删除成功", examService.deleteQuestion(id, questionId));
    }


//    导出试卷**
    @GetMapping("/{id}/question/download")
    public void downloadQuestion(HttpServletResponse response,
                                 @PathVariable("id") Long id) {
        examService.downloadQuestion(response, id);
    }

//    获取学生成绩排名**
    @GetMapping("/{id}/ranking")
    public Result<PageVo<StudentExamVo>> selectRanking(@PathVariable("id") Long id,
                                                       @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize){
        return Result.success(examService.selectRanking(id, pageNum, pageSize));
    }

//    导出学生成绩排名**
    @GetMapping("/{id}/ranking/download")
    public void downloadRanking(HttpServletResponse response,
                                @PathVariable("id") Long id) {
        examService.downloadRanking(response, id);
    }

//    获取测验的统计数据**
    @GetMapping("/{id}/statistics")
    public Result<StatisticsDto> selectStatistics(@PathVariable("id") Long id){
        return Result.success(examService.selectStatistics(id));
    }

}
