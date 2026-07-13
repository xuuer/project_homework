package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zhangjiu
* @description 针对表【teacher】的数据库操作Mapper
* @createDate 2025-06-20 20:56:41
* @Entity edu.fdzc.project.entity.Teacher
*/
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

}




