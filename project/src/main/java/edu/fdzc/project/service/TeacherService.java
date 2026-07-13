package edu.fdzc.project.service;

import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;

/**
* @author zhangjiu
* @description 针对表【teacher】的数据库操作Service
* @createDate 2025-06-20 20:56:41
*/
public interface TeacherService extends IService<Teacher> {

    Object register(LoginDto loginDto);

    Object login(LoginDto loginDto);

    Teacher selectById();

    Object updateStudentById(Teacher student);

    Object updatePassword(PasswordDto passwordDto);

    Object deleteById();
}
