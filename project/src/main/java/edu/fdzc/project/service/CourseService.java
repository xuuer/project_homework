package edu.fdzc.project.service;

import edu.fdzc.project.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.fdzc.project.entity.dto.QuestionDto;
import edu.fdzc.project.entity.vo.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author zhangjiu
 * @description 针对表【course】的数据库操作Service
 * @createDate 2025-06-20 22:54:05
 */
public interface CourseService extends IService<Course> {
    CourseVo selectById(Long id);

    PageVo<Course> selectPage(Long pageNum, Long pageSize, Course course);

    Object insert(Course course);

    Object updateByCouId(Course course);

    Object deleteById(Long id);

    Long getTeacherHomeworkCount();

    PageVo<Exam> selectExamPage(Long id, Long pageNum, Long pageSize, Exam exam);

    List<CategoryResourceVo> selectResource(Long id);

    PageVo<Student> selectStudentPage(Long id, Long pageNum, Long pageSize, Student student);

    Object insertStudent(Long id, String[] nos);

    Object deleteStudent(Long id, Long studentId);

    void downloadStudent(HttpServletResponse response, Long id);

    TagVo selectTag(Long id);

    StudentTagVo selectStudentTag(Long id, Long studentId);

    List<Resource> selectTagResource(Long id, Long tagId);

    Question selectPractice(Long id, Long tagId);

    Object submitPractice(QuestionDto dto);

    PageVo<Homework> selectHomeworkPage(Long id, Long pageNum, Long pageSize, Homework homework);

    Long getCourseHomeworkCount(Long courseId);

//    object
}
