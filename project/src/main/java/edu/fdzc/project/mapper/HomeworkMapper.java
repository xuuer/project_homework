package edu.fdzc.project.mapper;

import edu.fdzc.project.entity.Homework;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.fdzc.project.entity.HomeworkSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author zhangjiu
* @description 针对表【homework(作业表)】的数据库操作Mapper
* @createDate 2025-07-13 15:34:25
* @Entity edu.fdzc.project.entity.Homework
*/
@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {


}




