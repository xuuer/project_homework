package edu.fdzc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.common.Code;
import edu.fdzc.project.common.Result;
import edu.fdzc.project.entity.Student;
import edu.fdzc.project.entity.dto.LoginDto;
import edu.fdzc.project.entity.dto.PasswordDto;
import edu.fdzc.project.exception.CustomException;
import edu.fdzc.project.service.StudentService;
import edu.fdzc.project.mapper.StudentMapper;
import edu.fdzc.project.utils.JwtUtil;
import edu.fdzc.project.utils.Md5Util;
import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
* @author zhangjiu
* @description 针对表【student】的数据库操作Service实现
* @createDate 2025-06-20 20:56:41
*/
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService{

    @Autowired
    private StudentMapper studentMapper;

    @SneakyThrows
    @Override
    public Integer register(LoginDto loginDto) {
        //检查用户名
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getNo,loginDto.getUsername());
        Student student = studentMapper.selectOne(queryWrapper);
        if(student != null){
            throw new CustomException(Code.UNAUTHORIZED,"用户名已经注册");
        }

        Student stu = new Student();
        stu.setNo(loginDto.getUsername());
        stu.setPassword(Md5Util.getMD5String(loginDto.getPassword()));
        //设置时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);
        stu.setCreateTime(createTime);
        return studentMapper.insert(stu); //插入成功是1，失败是0
    }

    @SneakyThrows
    @Override
    public Object login(LoginDto loginDto) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getNo,loginDto.getUsername());
        Student student = studentMapper.selectOne(queryWrapper);
        if(student != null && Md5Util.getMD5String(loginDto.getPassword()).equals(student.getPassword())){
            return JwtUtil.createToken(student.getId(),"student");
        }
        throw new CustomException(Code.UNAUTHORIZED,"用户名或密码错误");

    }

    @Override
    public Student selectById() {
        Long id = JwtUtil.getUserId();
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getId,id);
        Student student = studentMapper.selectOne(queryWrapper);
        return student;
    }

    @Override
    public Integer updateStudentById(Student student) {
        Long id = JwtUtil.getUserId();
        student.setId(id);

        //设置修改时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);
        student.setUpdateTime(updateTime);
        // 修改个人信息
        return studentMapper.updateById(student);
    }

    @Override
    public Integer updatePassword(PasswordDto passwordDto) {
        Long id = JwtUtil.getUserId();
        Student student = studentMapper.selectById(id);
        //检查旧密码
        if(Md5Util.getMD5String(passwordDto.getOldPassword()).equals(student.getPassword())){
            student.setPassword(Md5Util.getMD5String(passwordDto.getNewPassword()));
            //跟新时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateTime = LocalDateTime.now().format(formatter);
            student.setUpdateTime(updateTime);
            return studentMapper.updateById(student);
        }
        return 0;
    }

    @Override
    public Object deleteById() {
        Long id = JwtUtil.getUserId();
        return studentMapper.deleteById(id);
    }


}




