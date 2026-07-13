package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Homework;
import edu.fdzc.project.entity.dto.HomeworkDto;
import edu.fdzc.project.entity.dto.StudentSubmissionHomeDto;
import edu.fdzc.project.entity.vo.HomeAssignmentVo;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.entity.vo.StudentHomeworkResultVo;
import edu.fdzc.project.entity.vo.StudentHomeworkVo;
import edu.fdzc.project.service.HomeworkService;
import edu.fdzc.project.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;

    //获取作业
    @GetMapping("/{id}")
    public Result<Homework> selectById(@PathVariable("id") Long id) {
        return Result.success(homeworkService.selectByHomeId(id));
    }

    //分页查询 - 修复后的版本，删除了重复的方法
    @GetMapping
    @Operation(summary = "分页查询作业")
    public Result<PageVo<Homework>> selectPage(@RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                               @RequestParam(name = "title", required = false) String title) {
        Homework homework = new Homework();
        homework.setTitle(title);
        System.out.println(homework);
        return Result.success(homeworkService.selectPage(pageNum, pageSize, homework));
    }

    //添加作业
    @PostMapping
    public Result<Object> insert(@RequestBody Homework homework) {
        System.out.println(homework);
        return Result.success("添加成功", homeworkService.insert(homework));
    }

    //修改作业
    @PutMapping
    public Result<Object> update(@RequestBody Homework homework) {
        return Result.success("修改成功", homeworkService.updateByHomeId(homework));
    }

    //删除作业
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable("id") Long id) {
        return Result.success("删除成功", homeworkService.deleteByid(id));
    }

    //增加文件
    @PostMapping("/{id}/home")
    public Result<Object> inertFile(@PathVariable("id") Long id, @RequestBody HomeworkDto homeworkDto) {
        return Result.success("增加成功", homeworkService.insertFile(id, homeworkDto));
    }

    //删除作业中的文件
    @DeleteMapping("/{id}/home/{homeAsidnId}")
    public Result<Object> deleteFile(@PathVariable("id") Long id, @PathVariable Long homeAsidnId) {
        return Result.success("删除成功", homeworkService.deleteByAId(homeAsidnId));
    }

    //更改作业中的内容
    @PutMapping("/{id}/home")
    public Result<Object> updateFile(@PathVariable("id") Long id, @RequestBody HomeworkDto homeworkDto) {
        return Result.success("修改成功", homeworkService.updateByHomAssigId(id, homeworkDto));
    }

    //查看作业的详细信息
    @GetMapping("/{id}/home")
    public Result<HomeAssignmentVo> selectByHomeAssigId(@PathVariable("id") Long id) {
        return Result.success(homeworkService.selectByHomeAssigId(id));
    }

    //学生提交作业
    @PostMapping("/{id}/sub")
    public Result<Object> insertStuHome(@PathVariable("id") Long id, @RequestBody StudentSubmissionHomeDto studentSubmissionHomeDto) {
        return Result.success("提交完成", homeworkService.insertStuHome(id, studentSubmissionHomeDto));
    }

    // 删除作业的文件
    @DeleteMapping("/{id}/sub/{homeAsidnId}")
    public Result<Object> deleteStuHomefile(@PathVariable("id") Long id, @PathVariable Long homeAsidnId) {
        return Result.success("删除成功", homeworkService.deleteByStuSubFile(homeAsidnId));
    }

    // 更改作业
    @PutMapping("/{id}/sub")
    public Result<Object> updateStuHome(@PathVariable("id") Long id, @RequestBody StudentSubmissionHomeDto studentSubmissionHomeDto) {
        return Result.success("修改完成", homeworkService.updateStuHome(id, studentSubmissionHomeDto));
    }

    //返回所有学生的信息
    @GetMapping("/{id}/result")
    public Result<PageVo<StudentHomeworkVo>> selectStuHomeList(@PathVariable("id") Long id,
                                                               @RequestParam(name = "pageNum", defaultValue = "1") Long pageNum,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Long pageSize,
                                                               @RequestParam(name = "status", defaultValue = "") String status) {
        return Result.success(homeworkService.selectStuHomeList(id, pageSize, pageNum, status));
    }

    //获取学生作业详情
    @GetMapping("/{id}/home/{stuId}/result")
    public Result<StudentHomeworkResultVo> selectStuHome(@PathVariable("id") Long id, @PathVariable Long stuId) {
        return Result.success(homeworkService.selectStuHomeResult(id, stuId));
    }

    //学生获取作业详情
    @GetMapping("/{id}/home/result")
    public Result<StudentHomeworkResultVo> selectStuBYStu(@PathVariable("id") Long id) {
        System.out.println(JwtUtil.getUserId());
        return Result.success(homeworkService.selectStuHomeResult(id, JwtUtil.getUserId()));
    }

    //老师批改学生的作业 - 修复版本
    @PostMapping("/{id}/home/{stuId}/update")
    @Operation(summary = "老师批改学生作业")
    public Result<Object> updateStuHomeWork(@PathVariable("id") Long id,
                                            @PathVariable("stuId") Long stuId,
                                            @RequestBody(required = false) StudentSubmissionHomeDto studentSubmissionHomeDto) {
        // 参数验证
        if (studentSubmissionHomeDto == null) {
            studentSubmissionHomeDto = new StudentSubmissionHomeDto();
        }

        // 如果没有提供评分，可以设置默认值或抛出异常
        if (studentSubmissionHomeDto.getScore() == null) {
            return Result.error(400, "请提供评分");
        }

        return Result.success("批改成功", homeworkService.updateStudentHomework(id, stuId, studentSubmissionHomeDto));
    }

    // 新增：简化的批改接口，只需要评分和评语
    @PostMapping("/{id}/home/{stuId}/grade")
    @Operation(summary = "快速批改学生作业")
    public Result<Object> gradeStudentHomework(@PathVariable("id") Long id,
                                               @PathVariable("stuId") Long stuId,
                                               @RequestParam("score") Integer score,
                                               @RequestParam(value = "comment", required = false) String comment) {
        StudentSubmissionHomeDto dto = new StudentSubmissionHomeDto();
        dto.setScore(score);
        dto.setComment(comment);
        return Result.success("批改成功", homeworkService.updateStudentHomework(id, stuId, dto));
    }
}