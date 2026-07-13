package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Course;
import edu.fdzc.project.entity.CourseStudent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author zhangjiu
* @description 针对表【course_student】的数据库操作Mapper
* @createDate 2025-06-20 23:29:19
* @Entity edu.fdzc.project.entity.CourseStudent
*/
@Mapper
public interface CourseStudentMapper extends BaseMapper<CourseStudent> {

    @Select("select c.* from course_student cs left join course c on c.id=cs.course_id where cs.student_id=#{id} limit #{size} offset #{offset}")
    List<Course> selectCoursePage(@Param("id") Long id, @Param("offset") Long offset, @Param("size") Long size);

    List<Student> selectStudentPage(@Param("id") Long id, @Param("offset") Long offset, @Param("size") Long size, @Param("no") String no, @Param("name") String name);

    Long selectStudentCount(@Param("id") Long id, @Param("no") String no, @Param("name") String name);
}




