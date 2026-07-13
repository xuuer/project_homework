package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.*;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.vo.*;
import edu.fdzc.project.service.CourseService;
import edu.fdzc.project.service.HomeworkService;
import edu.fdzc.project.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private HomeworkService homeworkService;

    //获取指定ID的班课信息 <>
    @GetMapping("/{id}")
    @Operation(summary = "获取指定ID的班课信息")
    public Result<CourseVo> selectById(@PathVariable("id") Long id){
        return Result.success(courseService.selectById(id));
    }

    //获取条件分页班课列表 <>
    @GetMapping
    @Operation(summary = "获取条件分页班课列表")
    public Result<PageVo<Course>> selectPage(@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                             Course course){
        return Result.success(courseService.selectPage(pageNum, pageSize, course));
    }

    //添加班课 <>
    @PostMapping
    @Operation(summary = "添加班课")
    public Result<Object> insert(@RequestBody Course course){
        return Result.success("添加成功", courseService.insert(course));
    }

    //修改班课<>
    @PutMapping
    @Operation(summary = "修改班课")
    public Result<Object> updateById(@RequestBody Course course){
        return Result.success("修改成功", courseService.updateByCouId(course));
    }

    //删除班课 **
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班课")
    public Result<Object> deleteById(@PathVariable("id") Long id){
        return Result.success("删除成功", courseService.deleteById(id));
    }

    //-----------------------------------------------------------------------------------------------------------------**
// **
    // 获取班课的测验列表
    @GetMapping("/{id}/exam")
    @Operation(summary = "获取班课的测验列表")
    public Result<PageVo<Exam>> selectExamPage(@PathVariable("id") Long id,
                                               @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                               Exam exam){
        return Result.success(courseService.selectExamPage(id, pageNum, pageSize, exam));
    }
    // **
    //获取班课的资源列表
    @GetMapping("/{id}/resource")
    @Operation(summary = "获取班课的资源列表")
    public Result<List<CategoryResourceVo>> selectResource(@PathVariable("id") Long id){
        return Result.success(courseService.selectResource(id));
    }
    // -----------------------------------------------------------------------------------------------------------
    //获取班课的学生列表
    @GetMapping("/{id}/student")
    @Operation(summary = "获取班课的学生列表")
    public Result<PageVo<Student>> selectStudentPage(@PathVariable("id") Long id,
                                                     @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                                     Student student){
        return Result.success(courseService.selectStudentPage(id, pageNum, pageSize, student));
    }

    //添加班课学生
    @PostMapping("/{id}/student")
    @Operation(summary = "添加班课学生")
    public Result<Object> insertStudent(@PathVariable("id") Long id,
                                        @RequestBody String[] nos){
        return Result.success("添加成功", courseService.insertStudent(id, nos));
    }

    //删除班课学生
    @DeleteMapping("{id}/student/{studentId}")
    @Operation(summary = "删除班课学生")
    public Result<Object> deleteResource(@PathVariable("id") Long id,
                                         @PathVariable("studentId") Long studentId) {
        return Result.success("删除成功", courseService.deleteStudent(id, studentId));
    }

    //导出班课学生
    @GetMapping("/{id}/student/download")
    @Operation(summary = "导出班课学生")
    public void downloadStudent(HttpServletResponse response,
                                @PathVariable("id") Long id) {
        courseService.downloadStudent(response, id);
    }

    //获取班课的知识点
    @GetMapping("/{id}/tag")
    @Operation(summary = "获取班课知识点")
    public Result<TagVo> selectTag(@PathVariable("id") Long id) {
        return Result.success(courseService.selectTag(id));
    }

    //获取学生只是带你掌握请况
    @GetMapping("/{id}/student/{studentId}/tag")
    @Operation(summary = "获取学生知识点掌握情况")
    public Result<StudentTagVo> selectStudentTag(@PathVariable("id") Long id,
                                                 @PathVariable("studentId") Long studentId) {
        return Result.success(courseService.selectStudentTag(id, studentId));
    }

    @GetMapping("/{id}/student/tag")
    @Operation(summary = "获取学生知识点掌握情况")
    public Result<StudentTagVo> selectStudentTag(@PathVariable("id") Long id) {
        return selectStudentTag(id, JwtUtil.getUserId());
    }

    //获取知识点资源
    @Operation(summary = "获取知识点资源")
    @GetMapping("/{id}/tag/{tagId}/resource")
    public Result<List<Resource>> selectTagResource(@PathVariable("id") Long id,
                                                    @PathVariable("tagId") Long tagId){
        return Result.success(courseService.selectTagResource(id, tagId));
    }

    @Operation(summary = "获取知识点练习")
    @GetMapping("/{id}/tag/{tagId}/practice")
    public Result<Question> selectPractice(@PathVariable("id") Long id,
                                           @PathVariable("tagId") Long tagId){
        return Result.success(courseService.selectPractice(id, tagId));
    }

    @Operation(summary = "提交知识点练习")
    @PostMapping("/practice")
    public Result<Object> submitPractice(@RequestBody QuestionDto dto){
        return Result.success("提交成功", courseService.submitPractice(dto));
    }

    // 获取班课的作业列表
    @GetMapping("/{id}/homework")
    @Operation(summary = "获取班课的作业列表")
    public Result<PageVo<Homework>> selectHomeworkPage(@PathVariable("id") Long id,
                                                       @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                                       Homework homework){
        return Result.success(courseService.selectHomeworkPage(id, pageNum, pageSize, homework));
    }

    @GetMapping("/teacher/homework/statistics")
    @Operation(summary = "获取教师作业统计")
    public Result<Long> getHomeworkCount() {
        return Result.success(courseService.getTeacherHomeworkCount());
    }

    @GetMapping("/{id}/homework/count")
    @Operation(summary = "获取课程作业数量")
    public Result<Long> getCourseHomeworkCount(@PathVariable("id") Long courseId) {
        return Result.success(courseService.getCourseHomeworkCount(courseId));
    }
}
