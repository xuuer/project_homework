package edu.fdzc.project.controller;

import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;
import edu.fdzc.project.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StrudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public Result<Object> register(@RequestBody LoginDto loginDto) {
        return Result.success("注册成功",studentService.register(loginDto));
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody LoginDto loginDto) {
        return Result.success("登录成功",studentService.login(loginDto));
    }

    @GetMapping
    public Result<Student> getStudentById() {
        return Result.success(studentService.selectById());
    }

    @PutMapping
    public Result<Object> updateStudentById(@RequestBody Student student) {
        return Result.success("修改成功",studentService.updateStudentById(student));
    }

    @PutMapping("/password")
    public Result<Object> updatePasswordById(@RequestBody PasswordDto passwordDto) {
        return Result.success("修改成功",studentService.updatePassword(passwordDto));
    }

    @DeleteMapping
    public Result<Object> deleteStudentById() {
        return Result.success("注销成功",studentService.deleteById());
    }

}
