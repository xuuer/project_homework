package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.Teacher;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;
import edu.fdzc.project.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/register")
    public Result<Object> register(@RequestBody LoginDto loginDto) {
        return Result.success("注册成功",teacherService.register(loginDto));
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginDto loginDto) {
        System.out.println(loginDto);
        return Result.success("登录成功",teacherService.login(loginDto));
    }

    @GetMapping
    public Result<Teacher> getStudentById() {
        System.out.println("查找老师的信息");
        return Result.success(teacherService.selectById());
    }

    @PutMapping
    public Result<Object> updateStudentById(@RequestBody Teacher student) {
        return Result.success("修改成功",teacherService.updateStudentById(student));
    }

    @PutMapping("/password")
    public Result<Object> updatePasswordById(@RequestBody PasswordDto passwordDto) {
        return Result.success("修改成功",teacherService.updatePassword(passwordDto));
    }

    @DeleteMapping
    public Result<Object> deleteStudentById() {
        return Result.success("注销成功",teacherService.deleteById());
    }

}