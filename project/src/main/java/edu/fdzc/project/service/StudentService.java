package edu.fdzc.project.service;

import edu.fdzc.project.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;

/**
* @author zhangjiu
* @description 针对表【student】的数据库操作Service
* @createDate 2025-06-20 20:56:41
*/
public interface StudentService extends IService<Student> {

    Object register(LoginDto loginDto);

    Object login(LoginDto loginDto);

    Student selectById();

    Object updateStudentById(Student student);

    Object updatePassword(PasswordDto passwordDto);

    Object deleteById();
}
