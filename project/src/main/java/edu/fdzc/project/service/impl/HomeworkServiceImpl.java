package edu.fdzc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.fdzc.project.common.Code;
import edu.fdzc.project.entity.*;
import edu.fdzc.project.entity.dto.HomeworkDto;
import edu.fdzc.project.entity.dto.StudentSubmissionHomeDto;
import edu.fdzc.project.entity.vo.HomeAssignmentVo;
import edu.fdzc.project.entity.vo.PageVo;
import edu.fdzc.project.entity.vo.StudentHomeworkResultVo;
import edu.fdzc.project.entity.vo.StudentHomeworkVo;
import edu.fdzc.project.exception.CustomException;
import edu.fdzc.project.mapper.*;
import edu.fdzc.project.service.HomeworkService;
import edu.fdzc.project.utils.JwtUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjiu
 * @description 针对表【homework(作业表)】的数据库操作Service实现
 * @createDate 2025-07-13 15:34:25
 */
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework>
        implements HomeworkService{

    @Autowired
    private HomeworkMapper homeworkMapper;

    @Autowired
    private HomeworkAttachmentMapper homeworkAttachmentMapper;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Autowired
    private HomeworkSubmissionFileMapper homeworkSubmissionFileMapper;

    @Autowired
    private  StudentMapper studentMapper;

    @Override
    @SneakyThrows
    public Homework selectByHomeId(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if(homework==null){
            throw new CustomException(Code.LOGICAL_ERROR,"不存在此作业");
        }
        List<HomeworkAttachment> attachments = homeworkAttachmentMapper.selectByHomeAssigId(homework.getId());
        homework.setAttachments(attachments);
        return homework;
    }

    @Override
    public PageVo<Homework> selectPage(Long pageNum, Long pageSize, Homework homework) {
        Long id = JwtUtil.getUserId();
        String title = homework.getTitle();
        LambdaQueryWrapper<Homework> queryWrapper = new LambdaQueryWrapper<>();
        //通过TeachertID  来查找
        queryWrapper.eq(Homework::getTeacherId, id);

        if(title!=null && !title.isEmpty()){
            //根据作业名称来查询
            queryWrapper.like(Homework::getTitle, title);
        }
        //按照结束时间排序
        queryWrapper.orderByDesc(Homework::getDeadline);

        Page<Homework> res = homeworkMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        PageVo<Homework> vo = new PageVo<>();

        vo.setList(res.getRecords()); //分页数据列表
        vo.setTotal(res.getTotal()); // 查询总数

        return vo;

    }

    @Override
    public Object insert(Homework homework) {
        Long teacherId = JwtUtil.getUserId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createTime = LocalDateTime.now().format(formatter);

//        homework.setHomeworkScore();
        homework.setTeacherId(teacherId); //设置老师的id
        homework.setCreateTime(createTime); // 设置创建时间

        return homeworkMapper.insert(homework);
    }

    @Override
    public Object updateByHomeId(Homework homework) {

        Long teacherId = JwtUtil.getUserId();
        homework.setTeacherId(teacherId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String updateTime = LocalDateTime.now().format(formatter);
        homework.setUpdateTime(updateTime);

        return homeworkMapper.updateById(homework);
    }

    @Override
    public Object deleteByid(Long id) {
//        这个id 是homework;
        return homeworkMapper.deleteById(id);
    }

    @Override
    @SneakyThrows
    public Object insertFile(Long id, HomeworkDto homeworkDto) {
        Homework homewoek = homeworkMapper.selectById(id);
        if(homewoek==null){
            throw new CustomException(Code.LOGICAL_ERROR,"没有这个作业");
        }
        homewoek.setDescription(homeworkDto.getDescription()); //设置作业内容
        homewoek.setAnswer(homeworkDto.getAnswer()); //设置答案 可以是图片
        homeworkMapper.updateById(homewoek);
        for(HomeworkAttachment attachment : homeworkDto.getAttachments()){
            attachment.setHomeworkId(id); //设置作业时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateTime = LocalDateTime.now().format(formatter);
            homewoek.setUpdateTime(updateTime);
            homeworkAttachmentMapper.insert(attachment);
        }
        return 1;
    }

    //删除文件
    @Override
    public Object deleteByAId(Long id) {
        return homeworkAttachmentMapper.deleteById(id);
    }

    @Override
    @SneakyThrows
    public Object updateByHomAssigId(Long id, HomeworkDto homeworkDto) {
        Homework homework = homeworkMapper.selectById(id);
        if(homework==null){
            throw new CustomException(Code.LOGICAL_ERROR,"作业不存在");
        }
        homework.setDescription(homeworkDto.getDescription());
        //新增答案
        homework.setAnswer(homeworkDto.getAnswer());
        homeworkMapper.updateById(homework); // 更新作业文件
        for(HomeworkAttachment attachment : homeworkDto.getAttachments()){
            attachment.setHomeworkId(id);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String updateTime = LocalDateTime.now().format(formatter);
            homework.setUpdateTime(updateTime);
            homeworkAttachmentMapper.insert(attachment);
        }
        return 1;
    }

    @Override
    public HomeAssignmentVo selectByHomeAssigId(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        LambdaQueryWrapper<HomeworkAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkAttachment::getHomeworkId, id);

        List<HomeworkAttachment> attachments = homeworkAttachmentMapper.selectList(wrapper);
        HomeAssignmentVo vo = new HomeAssignmentVo();
        vo.setAttachments(attachments); //返回文件
        vo.setId(homework.getId()); //返回作业id
        vo.setDescription(homework.getDescription()); //返回作业内容
        vo.setDeadline(homework.getDeadline()); // 放回截止时间
        vo.setTitle(homework.getTitle()); // 返回标题
        vo.setCourseId(homework.getCourseId()); //返回课程id
        vo.setTeacherId(homework.getTeacherId()); // 返回老师的id
        vo.setCreateTime(homework.getCreateTime()); //创建时间
        vo.setUpdateTime(homework.getUpdateTime()); // 更新时间
        vo.setAnswer(homework.getAnswer());//参考答案
        return vo;
    }

    @Override
    @SneakyThrows
    public Object insertStuHome(Long id, StudentSubmissionHomeDto studentSubmissionHomeDto) {
        Long studentId = JwtUtil.getUserId();
        HomeworkSubmission homeSub = homeworkSubmissionMapper.selectByStuHomId(id,studentId);
        if(homeSub != null )
        {
            throw new CustomException(Code.LOGICAL_ERROR,"不可以重复提交");
        }

        Homework homework = homeworkMapper.selectById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(LocalDateTime.parse(homework.getDeadline(), formatter))) {
            throw new CustomException(Code.LOGICAL_ERROR,"作业已关闭");
        }
        HomeworkSubmission homeworkSubmission = new HomeworkSubmission();
        homeworkSubmission.setHomeworkId(id); //作业id

        homeworkSubmission.setStudentId(studentId); //学生id

        if(studentSubmissionHomeDto.getTextContent()!=null && !studentSubmissionHomeDto.getTextContent().isEmpty()){
            homeworkSubmission.setTextContent(studentSubmissionHomeDto.getTextContent());//学生的正文
        }

        homeworkSubmission.setSubmitTime(now.format(formatter)); //上传时间

        homeworkSubmission.setStatus("true"); //显示已经提交

        homeworkSubmissionMapper.insert(homeworkSubmission); // 学生作业表提交作业

        for(HomeworkSubmissionFile file : studentSubmissionHomeDto.getFiles()){
            file.setHomeworkId(id);
            file.setStudentId(studentId);
            file.setUploadTime(now.format(formatter));
            homeworkSubmissionFileMapper.insert(file);
        }
        return 1;
    }

    @Override
    public Object deleteByStuSubFile(Long id) {
        return homeworkSubmissionFileMapper.deleteById(id); // 删除提交的文件
    }

    @Override
    public Object updateStuHome(Long id,  StudentSubmissionHomeDto stuSubHomeDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Long studentId = JwtUtil.getUserId();
        HomeworkSubmission homework = homeworkSubmissionMapper.selectByStuHomId(id,studentId);
        if(stuSubHomeDto.getTextContent()!=null && stuSubHomeDto.getTextContent().isEmpty()){
            homework.setTextContent(stuSubHomeDto.getTextContent());
            homework.setStatus("true");
            homework.setStudentId(studentId);
            homework.setHomeworkId(id);
            homeworkSubmissionMapper.updateById(homework);
        }
        for(HomeworkSubmissionFile file : stuSubHomeDto.getFiles()){
            file.setHomeworkId(id);
            file.setStudentId(studentId);
            file.setUploadTime(now.format(formatter));
            homeworkSubmissionFileMapper.insert(file);
        }
        return 1;
    }

    @Override
    @SneakyThrows
    public StudentHomeworkResultVo selectStuHomeResult(Long id, Long stuId) {
        Student student = studentMapper.selectById(stuId);
        System.out.println(student);
        if(student == null){
            throw new CustomException(Code.LOGICAL_ERROR,"学生不存在");
        }
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getStudentId, stuId);
        wrapper.eq(HomeworkSubmission::getHomeworkId, id);
        HomeworkSubmission homeworkSubmission = homeworkSubmissionMapper.selectOne(wrapper);
        StudentHomeworkVo vo = new StudentHomeworkVo();
        vo.setStartTime(homeworkSubmission.getSubmitTime()); //学生的提交时间
        vo.setName(student.getName()); //学生的姓名
        vo.setNo(student.getNo()); //学生学号
        vo.setScore(homeworkSubmission.getScore()); //获取的分数
        vo.setComment(homeworkSubmission.getComment());
        vo.setId(student.getId()); //学生的ID
        vo.setStatus(homeworkSubmission.getStatus());
        StudentHomeworkResultVo resultVo = new StudentHomeworkResultVo();
        resultVo.setStudentHomeworkVo(vo); //封装所有学生的信息
        resultVo.setTextContent(homeworkSubmission.getTextContent()); //学生提交的答案

        LambdaQueryWrapper<HomeworkSubmissionFile> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(HomeworkSubmissionFile::getHomeworkId, id);
        fileWrapper.eq(HomeworkSubmissionFile::getStudentId, stuId);

        List<HomeworkSubmissionFile> homeworkSubmissionFile = homeworkSubmissionFileMapper.selectList(fileWrapper); //查询作业列表

        resultVo.setFiles(homeworkSubmissionFile);
        return resultVo;
    }

    @Override
    public PageVo<StudentHomeworkVo> selectStuHomeList(Long id, Long pageSize, Long pageNum, String status) {
        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId, id);
        if(status != null && !status.isEmpty()) {
            wrapper.eq(HomeworkSubmission::getStatus, status);
        }
        wrapper.orderByDesc(HomeworkSubmission::getSubmitTime); // 根据提交时间来排序
        Page<HomeworkSubmission> page = homeworkSubmissionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<StudentHomeworkVo> list = new ArrayList<>(); //学生作业返回列表

        List<HomeworkSubmission> records = page.getRecords(); //分页查询的列表数据

        for(HomeworkSubmission homeworkSubmission : records){ //遍历数据进行封装
            Student student = studentMapper.selectById(homeworkSubmission.getStudentId());
            StudentHomeworkVo vo = new StudentHomeworkVo();

            vo.setId(homeworkSubmission.getStudentId()); //学生ID
            vo.setName(student.getName()); //学生姓名
            vo.setNo(student.getNo()); //学生学号
            vo.setScore(homeworkSubmission.getScore()); //学生分数
            vo.setStatus(homeworkSubmission.getStatus()); //学生提交作业状态
            vo.setStartTime(homeworkSubmission.getSubmitTime()); // 学生提交时间

            list.add(vo);
        }
        PageVo<StudentHomeworkVo> pageVo = new PageVo<>();
        pageVo.setList(list);
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    @Override
    @SneakyThrows
    public Object updateStudentHomework(Long id, Long stuId, StudentSubmissionHomeDto studentSubmissionHomeDto) {
        // 参数验证
        if (studentSubmissionHomeDto == null) {
            throw new CustomException(Code.LOGICAL_ERROR, "批改信息不能为空");
        }

        if (studentSubmissionHomeDto.getScore() == null) {
            throw new CustomException(Code.LOGICAL_ERROR, "评分不能为空");
        }

        LambdaQueryWrapper<HomeworkSubmission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkSubmission::getHomeworkId, id);
        wrapper.eq(HomeworkSubmission::getStudentId, stuId);
        HomeworkSubmission homeworkSubmission = homeworkSubmissionMapper.selectOne(wrapper);

        if (homeworkSubmission == null) {
            throw new CustomException(Code.LOGICAL_ERROR, "未找到学生的作业提交记录");
        }

        // 设置评分
        homeworkSubmission.setScore(studentSubmissionHomeDto.getScore());

        // 修复逻辑错误：设置评语（之前的条件判断有误）
        if (studentSubmissionHomeDto.getComment() != null && !studentSubmissionHomeDto.getComment().isEmpty()) {
            homeworkSubmission.setComment(studentSubmissionHomeDto.getComment());
        }

        // 设置批改状态
        homeworkSubmission.setChangStatus("true");
        homeworkSubmission.setStatus("已批阅");
        return homeworkSubmissionMapper.updateById(homeworkSubmission);
    }

    @Override
    public PageVo<Homework> selectPageByCourse(Long pageNum, Long pageSize, Homework homework) {
        Long courseId = homework.getCourseId();
        String title = homework.getTitle();

        LambdaQueryWrapper<Homework> queryWrapper = new LambdaQueryWrapper<>();
        // 通过CourseID来查找
        queryWrapper.eq(Homework::getCourseId, courseId);

        if(title != null && !title.isEmpty()){
            // 根据作业名称来查询
            queryWrapper.like(Homework::getTitle, title);
        }
        // 按照结束时间排序
        queryWrapper.orderByDesc(Homework::getDeadline);

        Page<Homework> res = homeworkMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

        PageVo<Homework> vo = new PageVo<>();
        vo.setList(res.getRecords()); // 分页数据列表
        vo.setTotal(res.getTotal()); // 查询总数

        return vo;
    }
}