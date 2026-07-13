package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhangjiu
* @description 针对表【course】的数据库操作Mapper
* @createDate 2025-06-20 22:54:05
* @Entity edu.fdzc.project.entity.Course
*/
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}




