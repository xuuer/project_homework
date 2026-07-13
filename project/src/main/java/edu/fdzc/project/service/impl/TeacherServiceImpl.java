package edu.fdzc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.common.Code;
import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.Teacher;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;
import edu.fdzc.project.exception.CustomException;
import edu.fdzc.project.mapper.StudentMapper;
import edu.fdzc.project.service.TeacherService;
import edu.fdzc.project.mapper.TeacherMapper;
import edu.fdzc.project.utils.JwtUtil;
import edu.fdzc.project.utils.Md5Util;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* @author zhangjiu
* @description 针对表【teacher】的数据库操作Service实现
* @createDate 2025-06-20 20:56:41
*/
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService{

    @Autowired
    private TeacherMapper teacherMapper;

    @SneakyThrows
    @Override
    public Integer register(LoginDto loginDto) {
        //检查用户名
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getNo,loginDto.getUsername());
        Teacher teacher = teacherMapper.selectOne(queryWrapper);
        if(teacher != null){
            throw new CustomException(Code.UNAUTHORIZED,"用户名已经注册");
        }

        Teacher stu = new Teacher();
        stu.setNo(loginDto.getUsername());
        stu.setPassword(Md5Util.getMD5String(loginDto.getPassword()));
        //设置时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);
        stu.setCreateTime(createTime);
        return teacherMapper.insert(stu); //插入成功是1，失败是0
    }

    @SneakyThrows
    @Override
    public Object login(LoginDto loginDto) {
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getNo,loginDto.getUsername());
        Teacher student = teacherMapper.selectOne(queryWrapper);
        if(student != null && Md5Util.getMD5String(loginDto.getPassword()).equals(student.getPassword())){
            return JwtUtil.createToken(student.getId(),"teacher");
        }
        throw new CustomException(Code.UNAUTHORIZED,"用户名或密码错误");
    }

    @Override
    public Teacher selectById() {
        Long id = JwtUtil.getUserId();
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getId,id);
        Teacher student = teacherMapper.selectOne(queryWrapper);
        return student;
    }

    @Override
    public Integer updateStudentById(Teacher student) {
        Long id = JwtUtil.getUserId();
        student.setId(id);

        //设置修改时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);
        student.setUpdateTime(updateTime);
        // 修改个人信息
        return teacherMapper.updateById(student);
    }

    @Override
    public Integer updatePassword(PasswordDto passwordDto) {
        Long id = JwtUtil.getUserId();
        Teacher student = teacherMapper.selectById(id);
        //检查旧密码
        if(Md5Util.getMD5String(passwordDto.getOldPassword()).equals(student.getPassword())){
            student.setPassword(Md5Util.getMD5String(passwordDto.getNewPassword()));
            //跟新时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateTime = LocalDateTime.now().format(formatter);
            student.setUpdateTime(updateTime);
            return teacherMapper.updateById(student);
        }
        return 0;
    }

    @Override
    public Object deleteById() {
        Long id = JwtUtil.getUserId();
        return teacherMapper.deleteById(id);
    }

    public static void main(String[] args) {
        System.out.println(Md5Util.getMD5String("123456"));
    }

}




